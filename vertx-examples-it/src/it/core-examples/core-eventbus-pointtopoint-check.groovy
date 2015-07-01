helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Exception")
helper.ensureTextNotContainedInClientErrorStream("Exception")

helper.ensureTextInOutputStream("pong")
helper.ensureTextInClientOutputStream("ping")
