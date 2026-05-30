import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile

plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
	id("maven-publish")
	id("java")
}

fun prop(vararg names: String): String {
	for (name in names) {
		if (project.hasProperty(name)) return project.property(name).toString()
	}
	error("Missing required Gradle property: ${names.joinToString(" or ")}")
}

val minecraftVersion = prop("build.minecraft_version")
val yarnMappings = prop("build.yarn_mappings")
val loaderVersion = prop("loader_version")
val owoVersion = prop("deps.owo_version")
val modVersion = prop("mod_version")
val mavenGroup = prop("maven_group")
val archivesBaseName = prop("archives_base_name")
val fabricVersion = prop("build.fabric_api_version")

version = modVersion
group = mavenGroup

base {
	archivesName.set(archivesBaseName)
}

repositories {
	maven(url = uri("https://maven.wispforest.io"))
}

loom {
	splitEnvironmentSourceSets()

	mods {
		create("tabmanager") {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.named("client").get())
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings("net.fabricmc:yarn:$yarnMappings:v2")
	modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
	modImplementation("io.wispforest:owo-lib:$owoVersion")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
	include("io.wispforest:owo-sentinel:$owoVersion")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.version))
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(21)
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	inputs.property("archivesName", base.archivesName.get())
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = archivesBaseName
			from(components["java"])
		}
	}
}