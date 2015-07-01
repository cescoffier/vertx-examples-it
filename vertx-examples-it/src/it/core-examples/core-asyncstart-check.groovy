helper.ensureSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Exception")

helper.ensureTextInOutputStream("Main verticle has started, let's deploy some others...")
helper.ensureTextInOutputStream("In OtherVerticle.start (async)")
helper.ensureTextInOutputStream("Other verticle deployed ok")
helper.ensureTextInOutputStream("Startup tasks are now complete, OtherVerticle is now started!")
helper.ensureTextInOutputStream("Cleanup tasks are now complete, OtherVerticle is now stopped!")
helper.ensureTextInOutputStream("Undeployed ok!")
