all:
	sbt bot/assembly
	docker build -t pepeground/pepeground-bot .
	docker push pepeground/pepeground-bot
	kubectl replace --force -f ./kubernetes/deployment.yaml
