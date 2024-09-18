package com.canonical.rockcraft.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a verbose list of commands to generate Java runtime
 */
public class RawRuntimePart implements IRuntimeProvider {

    private final RockcraftOptions options;

    public RawRuntimePart(RockcraftOptions options) {
        this.options = options;
    }

    private void append(StringBuffer buffer, String str) {
        buffer.append(str);
        buffer.append("\n");
    }

    @Override
    public Map<String, Object> getRuntimePart(List<File> files) {
        var part = new HashMap<String, Object>();
        part.put("plugin", "nil");
        part.put("build-packages", new String[]{options.getBuildPackage()});

        var jarList = new StringBuffer();
        for (var jar : files) {
            if (!jarList.isEmpty())
                jarList.append(" ");
            jarList.append(String.format("${CRAFT_STAGE}/jars/%s", jar.getName()));
        }

        var commands = new StringBuffer();
        append(commands, "JAVA_HOME=$(dirname $(dirname $(readlink -f /usr/bin/java)))");
        append(commands, "JAVA_HOME=${JAVA_HOME:1}");
        append(commands, "PROCESS_JARS=" + jarList);
        append(commands, "mkdir -p ${CRAFT_PART_BUILD}/tmp");
        append(commands,
                "(cd ${CRAFT_PART_BUILD}/tmp && for jar in ${PROCESS_JARS}; do jar xvf ${jar}; done;)"
        );
        append(commands, "CPATH=$(find ${CRAFT_PART_BUILD}/tmp -type f -name *.jar)");
        append(commands, "CPATH=$(echo ${CPATH}:. | sed s'/[[:space:]]/:/'g)");
        append(commands, "echo ${CPATH}");
        append(commands, String.format("""
                if [ "x${PROCESS_JARS}" != "x" ]; then
                deps=$(jdeps --class-path=${CPATH} -q --recursive  --ignore-missing-deps \
                    --print-module-deps --multi-release %d ${PROCESS_JARS}); else deps=java.base; fi
                """, options.getTargetRelease()));
        append(commands, "INSTALL_ROOT=${CRAFT_PART_INSTALL}/${JAVA_HOME}");
        append(commands,
                "rm -rf ${INSTALL_ROOT} && jlink --add-modules ${deps} --output ${INSTALL_ROOT}"
        );

        append(commands,
                "(cd ${CRAFT_PART_INSTALL} && mkdir -p usr/bin && ln -s --relative ${JAVA_HOME}/bin/java usr/bin/)");
        part.put("override-build", commands.toString());
        part.put("after", new String[]{"gradle/rockcraft/dump", "gradle/rockcraft/deps"});
        return part;
    }
}
