package code123.games.crystal.teavm;

import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

/** Builds the TeaVM/HTML application. */
@SkipClass
public class TeaVMBuilder {
    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/packed/", FileType.Absolute, "packed/")) ;
        teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/uiskin/", FileType.Absolute, "uiskin/")) ;
            teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/story/", FileType.Absolute, "story/")) ;
        teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/maps/", FileType.Absolute, "maps/")) ;
        teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/sounds/", FileType.Absolute, "sounds/")) ;
        teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/music/", FileType.Absolute, "music/")) ;
        teaBuildConfiguration.assetsPath
            .add(AssetFileHandle.createCopyHandle("../assets/animations/", FileType.Absolute, "animations/",
             (file, type, op) -> {
                // Ignore files with in parts folder
                if (file.contains("/parts/")) {
                    return false;
                }
                return true;
             })) ;

        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();

        // Register any extra classpath assets here:
        // teaBuildConfiguration.additionalAssetsClasspathFiles.add("code123/games/crystal/asset.extension");

        // Register any classes or packages that require reflection here:
        // TeaReflectionSupplier.addReflectionClass("code123.games.crystal.reflect");

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setMainClass(TeaVMLauncher.class.getName());
        // For many (or most) applications, using the highest optimization won't add much to build time.
        // If your builds take too long, and runtime performance doesn't matter, you can change FULL to SIMPLE .
        tool.setOptimizationLevel(TeaVMOptimizationLevel.FULL);
        tool.setObfuscated(true);
        TeaBuilder.build(tool);
    }
}
