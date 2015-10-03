
helper.ensureSucceededInDeployingVerticle()
helper.ensureTextInOutputStream("Waiting for single event")
helper.ensureTextInOutputStream("Single event has fired")

return true
