import static org.assertj.core.api.Assertions.*;

helper.ensureSucceededInDeployingVerticle()
helper.ensureClientSucceededInDeployingVerticle()
helper.ensureTextNotContainedInErrorStream("Unhandled Exception")
helper.ensureTextNotContainedInClientErrorStream("Unhandled Exception")

helper.ensureTextInOutputStream("Echo server is now listening")

def String o = helper.getClientOutput()
assertThat(o).containsSequence(
        "Net client sending", "hello 0", "hello 1", "hello 2", "hello 4", "hello 6", "hello 9")

assertThat(o).containsSequence("client receiving")

true

