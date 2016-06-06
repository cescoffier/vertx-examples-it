
helper.ensureSucceededInDeployingVerticle()
helper.ensureTextInOutputStream("user2@example.com")
helper.ensureTextInOutputStream("user3@example.com")
helper.ensureTextInOutputStream("user4@example.com")
helper.ensureTextInOutputStream("bounce@example.com")

return true
