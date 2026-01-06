.PHONY: deploy-test deploy-prod deploy-test-verbose deploy-prod-verbose

deploy-test:
	./deploy/deploy.sh test

deploy-prod:
	./deploy/deploy.sh prod

deploy-test-verbose:
	./deploy/deploy.sh test --verbose

deploy-prod-verbose:
	./deploy/deploy.sh prod --verbose
	
deploy-dom:
	./deploy/deploy.sh dom

deploy-dom-verbose:
	./deploy/deploy.sh dom --verbose
