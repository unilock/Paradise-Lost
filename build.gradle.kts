import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

val paradiseLostVersion: String by properties

val minecraft_version: String by properties
val yarn_mappings: String by properties
val loader_version: String by properties
val javaVersion: String by properties

val fabric_version: String by properties
val customportalapiVersion: String by properties
val cardinalComponentsVersion: String by properties
val crowdinTranslateVersion: String by properties
val terraformVersion: String by properties

plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("com.modrinth.minotaur") version "1.2.1"
    id("de.guntram.mcmod.crowdin-translate") version "1.4+1.19"
    `maven-publish`
    checkstyle
}

version = paradiseLostVersion
group = "net.id"

repositories {
    mavenCentral()

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }

    maven {
        name = "Hephaestus"
        url = uri("https://hephaestus.dev/release")
    }

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }

    maven {
        name = "Ladysnake Mods"
        url = uri("https://maven.ladysnake.org/releases")
    }

    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }

    maven {
        name = "Kyrptonaught"
        url = uri("https://maven.kyrptonaught.dev/")
    }

    maven {
        name = "JamesWhiteShirt"
        url = uri("https://maven.jamieswhiteshirt.com/libs-release/")
    }

    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
}

dependencies {
    minecraft(
            group = "com.mojang",
            name = "minecraft",
            version = minecraft_version,
    )

    mappings(
            group = "net.fabricmc",
            name = "yarn",
            version = yarn_mappings,
            classifier = "v2",
    )

    modImplementation(
            group = "net.fabricmc",
            name = "fabric-loader",
            version = loader_version,
    )

    modImplementation(
            group = "org.ladysnake.cardinal-components-api",
            name = "cardinal-components-base",
            version = cardinalComponentsVersion,
    ).also(::include)

    modImplementation(
            group = "org.ladysnake.cardinal-components-api",
            name = "cardinal-components-entity",
            version = cardinalComponentsVersion,
    ).also(::include)

    modImplementation(
            group = "net.kyrptonaught",
            name = "customportalapi",
            version = customportalapiVersion,
    ).also(::include).exclude(module = "sodium")

    modImplementation(
            group = "net.fabricmc.fabric-api",
            name = "fabric-api",
            version = fabric_version,
    )

    modImplementation(
            group = "com.terraformersmc.terraform-api",
            name = "terraform-wood-api-v1",
            version = terraformVersion,
    ).also(::include)
}

tasks {
    processResources {
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    build {
        dependsOn(downloadTranslations)
    }

    jar {
        manifest {
            attributes(
                    "Implementation-Title" to "ParadiseLost",
                    "Implementation-Version" to paradiseLostVersion,
                    "Main-Class" to "net.id.paradiselost.executable.InstallerGUI",
            )
        }

        from("LICENSE.md")
    }

    withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "400"))
    }
}

base {
    archivesName.set("paradise-lost")
}

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    withSourcesJar()
}

loom {
    accessWidenerPath.set(file("src/main/resources/paradise_lost.accesswidener"))

    runs {
        getByName("client") {
            programArg("--username=${System.getProperty("user.name")}")
            runDir = "run/client"
            client()
        }

        getByName("server") {
            runDir = "run/server"
            server()
        }

        create("clientDebug") {
            inherit(getByName("client"))
            configName = "Minecraft Client (Mixin Debug)"
            vmArgs.add("-Dmixin.debug.export=true")
        }

        create("serverDebug") {
            inherit(getByName("server"))
            vmArgs.add("-Dmixin.debug.export=true")
            configName = "Minecraft Server (Mixin Debug)"
        }
    }
}

//task publishModrinth (type: TaskModrinthUpload) {
//    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//    System.out.println('Enter the modrinth auth token: ');
//    token = br.readLine(); // Get password
//    projectId = 'IKpsG0nF'
//    System.out.println('Enter the version number:');
//    versionNumber = br.readLine();
//    System.out.println('Enter the version name:');
//    versionName = br.readLine();
//    uploadFile = jar // This is the java jar task
//    System.out.println('Enter the game version number: (See minotaur docs for valids)');
//    addGameVersion(br.readLine());
//    System.out.println('Enter changelog:');
//    changelog = br.readLine();
//    addLoader('fabric')
//}

crowdintranslate {
    setCrowdinProjectname("aether")
    minecraftProjectName = "paradise_lost"
    verbose = true
}

fabricApi {
    configureDataGeneration()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

checkstyle {
    sourceSets = emptyList()
}
