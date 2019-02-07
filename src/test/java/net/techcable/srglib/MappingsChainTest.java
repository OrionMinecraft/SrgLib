package net.techcable.srglib;

import net.techcable.srglib.format.MappingsFormat;
import net.techcable.srglib.mappings.ImmutableMappings;
import net.techcable.srglib.mappings.Mappings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MappingsChainTest {
    @Parameterized.Parameters
    public static Object[][] testData() {
        Map<String, String> packageMappings = new HashMap<>();
        packageMappings.put("", "net.minecraft.server");

        return new Object[][] {
                {
                        new Mappings[]{
                                MappingsFormat.SEARGE_FORMAT.parseLines(
                                        "CL: aa Entity",
                                        "CL: ab Cow",
                                        "CL: ac EntityPlayer",
                                        "CL: ad World",
                                        "CL: ae Server"
                                ),
                                MappingsFormat.SEARGE_FORMAT.parseLines(
                                        "CL: af ForgetfulClass",
                                        "FD: Entity/a Entity/dead",
                                        "MD: Cow/a (LCow;)V Cow/love (LCow;)V",
                                        "MD: EntityPlayer/a (Ljava/lang/String;)V EntityPlayer/disconnect (Ljava/lang/String;)V",
                                        "FD: World/a World/time",
                                        "MD: World/a ()V World/tick ()V",
                                        "FD: Server/a Server/ticks",
                                        "MD: Server/a ()V Server/tick ()V"
                                ),
                                MappingsFormat.SEARGE_FORMAT.parseLines(
                                        "CL: ForgetfulClass me/stupid/ChangedMind",
                                        "FD: World/time World/numTicks",
                                        "MD: World/tick ()V World/pulse ()V"
                                ),
                                Mappings.createPackageMappings(packageMappings)
                        },
                        MappingsFormat.SEARGE_FORMAT.parseLines(
                                "CL: aa net/minecraft/server/Entity",
                                "CL: ab net/minecraft/server/Cow",
                                "CL: ac net/minecraft/server/EntityPlayer",
                                "CL: ad net/minecraft/server/World",
                                "CL: ae net/minecraft/server/Server",
                                "CL: af me/stupid/ChangedMind",
                                "FD: aa/a net/minecraft/server/Entity/dead",
                                "MD: ab/a (Lab;)V net/minecraft/server/Cow/love (Lnet/minecraft/server/Cow;)V",
                                "MD: ac/a (Ljava/lang/String;)V net/minecraft/server/EntityPlayer/disconnect (Ljava/lang/String;)V",
                                "FD: ad/a net/minecraft/server/World/numTicks",
                                "MD: ad/a ()V net/minecraft/server/World/pulse ()V",
                                "FD: ae/a net/minecraft/server/Server/ticks",
                                "MD: ae/a ()V net/minecraft/server/Server/tick ()V"
                        )
                }
        };
    }

    private final List<Mappings> mappings;
    private final ImmutableMappings expectedOutput;

    public MappingsChainTest(Mappings[] mappings, Mappings expectedOutput) {
        this.mappings = Arrays.asList(mappings);
        this.expectedOutput = expectedOutput.snapshot();
    }

    @Test
    public void testChaining() {
        ImmutableMappings chained = Mappings.chain(mappings).snapshot();
        assertEquals(expectedOutput, chained);
    }
}
