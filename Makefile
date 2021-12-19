all:
	sbt bot/assembly
	docker build -t pepeground/pepeground-bot .
	docker push pepeground/pepeground-bot
	kubectl rollout restart -n pepeground deployment/pepeground-bot
	kubectl rollout restart -n pepeground deployment/pepeground-learn
	kubectl rollout restart -n pepeground deployment/pepeground-cleanup
