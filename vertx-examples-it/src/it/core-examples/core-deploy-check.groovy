helper.ensureSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")

helper.ensureTextInOutputStream("Main verticle has started, let's deploy some others...")
helper.ensureTextInOutputStream("In OtherVerticle.start")
helper.ensureTextInOutputStream("Config is {\"foo\":\"bar\"}")
helper.ensureTextInOutputStream("In OtherVerticle.stop")
helper.ensureTextInOutputStream("Undeployed ok!")
