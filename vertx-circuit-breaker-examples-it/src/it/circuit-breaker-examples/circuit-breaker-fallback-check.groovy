helper.ensureSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")

helper.ensureTextInOutputStream("Result: Hello (fallback)")