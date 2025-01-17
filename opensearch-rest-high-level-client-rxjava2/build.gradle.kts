/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  `java-library`
  `maven-publish`
  signing
}

val vertxVersion = extra["vertxVersion"]

dependencies {
  api(rootProject)
  api("io.vertx:vertx-rx-java2:${vertxVersion}")
  compileOnly("io.vertx:vertx-codegen:${vertxVersion}")
  annotationProcessor("io.vertx:vertx-rx-java2:${vertxVersion}")
  annotationProcessor("io.vertx:vertx-rx-java2-gen:${vertxVersion}")
  annotationProcessor("io.vertx:vertx-codegen:${vertxVersion}:processor")
}

sourceSets {
  main {
    java {
      setSrcDirs(listOf("../src/main/generated", "src/main/generated"))
    }
  }
}

tasks {
  getByName<JavaCompile>("compileJava") {
    options.annotationProcessorGeneratedSourcesDirectory = File("$projectDir/src/main/generated")
  }

  getByName<Delete>("clean") {
    delete.add("src/main/generated")
  }

  getByName<Jar>("jar") {
    exclude("io/vertx/opensearch/client/*.class")
  }

  create<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    classifier = "sources"
  }

  create<Jar>("javadocJar") {
    from(javadoc)
    classifier = "javadoc"
  }

  javadoc {
    if (JavaVersion.current().isJava9Compatible) {
      (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
  }

  withType<Sign> {
    onlyIf { project.extra["isReleaseVersion"] as Boolean }
  }
}
publishing {

publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      pom {
        name.set(project.name)
        description.set("Reactiverse OpenSearch client :: RxJava2 bindings")
        url.set("https://github.com/reactiverse/opensearch-client")
        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("jponge")
            name.set("Julien Ponge")
            email.set("julien.ponge@gmail.com")
          }
          developer {
            id.set("sboeckelmann")
            name.set("Sven Böckelmann")
            email.set("sven.boeckelmann@googlemail.com")
          }
        }
        scm {
          connection.set("scm:git:git@github.com:reactiverse/opensearch-client.git")
          developerConnection.set("scm:git:git@github.com:reactiverse/opensearch-client.git")
          url.set("https://github.com/reactiverse/opensearch-client")
        }
      }
    }
  }
  repositories {
    // To locally check out the poms
    maven {
      val releasesRepoUrl = uri("$buildDir/repos/releases")
      val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
      name = "BuildDir"
      url = if (project.extra["isReleaseVersion"] as Boolean) releasesRepoUrl else snapshotsRepoUrl
    }
    maven {
      val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
      val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      name = "SonatypeOSS"
      url = if (project.extra["isReleaseVersion"] as Boolean) releasesRepoUrl else snapshotsRepoUrl
      credentials {
        val ossrhUsername: String by project
        val ossrhPassword: String by project
        username = ossrhUsername
        password = ossrhPassword
      }
    }
  }
}

signing {
  sign(publishing.publications["mavenJava"])
}
