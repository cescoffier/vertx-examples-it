helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")
helper.ensureTextNotContainedInClientErrorStream("Unhandled Exception")

helper.ensureTextInClientOutputStream("<html><body><h1>Hello from vert.x!</h1></body></html>")