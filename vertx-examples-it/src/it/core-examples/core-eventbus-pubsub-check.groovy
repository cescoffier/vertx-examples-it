helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Exception")
helper.ensureTextNotContainedInClientErrorStream("Exception")

helper.ensureTextInClientOutputStream("news")
