helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")
helper.ensureTextNotContainedInClientErrorStream("Unhandled Exception")

helper.ensureTextInClientOutputStream("Got response 200 with protocol HTTP_2")
helper.ensureTextInClientOutputStream("Got pushed data alert(\"hello world\");")
helper.ensureTextInClientOutputStream("Got data")

