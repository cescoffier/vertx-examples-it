
helper.ensureSucceededInDeployingVerticle()
helper.ensureTextInOutputStream("key stored")
helper.ensureTextInOutputStream("Retrieved value: value")

return true
