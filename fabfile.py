from fabric.api import env, run, sudo, local, put, settings
from contextlib import contextmanager
import os
import fabtools

env.hosts = ['pepeground']
env.shell = 'bash -c'
env.use_ssh_config = True

@contextmanager
def locally():
    global run
    global sudo
    global local
    _run, _sudo = run, sudo
    run = sudo = local
    yield
    run, sudo = _run, _sudo

def assembly():
    with locally():
        run("sbt assembly")


def production():
        env.project_user = run("whoami")
        env.sudo_user = env.project_user
        env.base_dir = "/home/%s/pepeground" % (env.project_user)

def setup():
        run("mkdir -p %s" % env.base_dir)

def upload():
        put("bot/target/scala-2.12/bot-assembly-0.1.jar", "%s/bot.jar" % (env.base_dir))

def restart():
        run("sudo systemctl restart pepeground")

def deploy():
        assembly()
        production()
        setup()
        upload()
        restart()
