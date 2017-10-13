helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")
helper.ensureTextNotContainedInClientErrorStream("Unhandled Exception")

helper.ensureTextInClientOutputStream("news")
helper.ensureTextInClientOutputStream("consumer 1")
helper.ensureTextInClientOutputStream("consumer 2")
helper.ensureTextInClientOutputStream("consumer 3")
