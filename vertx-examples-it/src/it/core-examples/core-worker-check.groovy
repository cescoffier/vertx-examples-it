helper.ensureSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled exception")

helper.ensureTextInOutputStream("[Worker] Consuming data in")
helper.ensureTextInOutputStream("[Main] Receiving reply ' HELLO VERT.X'")