helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")
helper.ensureTextNotContainedInClientErrorStream("Unhandled Exception")

helper.ensureTextInOutputStream("firstName: Dale")
helper.ensureTextInOutputStream("lastName: Cooper")
helper.ensureTextInOutputStream("male: true")
helper.ensureTextInClientOutputStream("Got HTTP response with status 200")