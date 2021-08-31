package org.jetbrains.dokka.versioning

import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.configuration
import org.jetbrains.dokka.templates.TemplatingPlugin
import versioning.DefaultPreviousDocumentationCopier
import versioning.VersionsNavigationAdder

class VersioningPlugin : DokkaPlugin() {

    val versioningHandler by extensionPoint<VersioningHandler>()
    val versionsNavigationCreator by extensionPoint<VersionsNavigationCreator>()
    val versionsOrdering by extensionPoint<VersionsOrdering>()

    private val dokkaBase by lazy { plugin<DokkaBase>() }
    private val templatingPlugin by lazy { plugin<TemplatingPlugin>() }

    val defaultVersioningHandler by extending {
        versioningHandler providing ::DefaultVersioningHandler
    }
    val defaultVersioningNavigationCreator by extending {
        versionsNavigationCreator providing ::HtmlVersionsNavigationCreator
    }
    val replaceVersionCommandHandler by extending {
        templatingPlugin.directiveBasedCommandHandlers providing ::ReplaceVersionCommandHandler
    }
    val resolveLinkConsumer by extending {
        dokkaBase.immediateHtmlCommandConsumer providing ::ReplaceVersionCommandConsumer
    }
    val cssStyleInstaller by extending {
        dokkaBase.htmlPreprocessors providing ::MultiModuleStylesInstaller order {
            after(dokkaBase.assetsInstaller)
            before(dokkaBase.baseSearchbarDataInstaller)
        }
    }
    val versionsDefaultOrdering by extending {
        versionsOrdering providing { ctx ->
            configuration<VersioningPlugin, VersioningConfiguration>(ctx)?.versionsOrdering?.let {
                ByConfigurationVersionOrdering(ctx)
            } ?: SemVerVersionOrdering()
        }
    }
 val previousDocumentationCopier by extending {
     dokkaBase.postActions providing ::DefaultPreviousDocumentationCopier applyIf { !delayTemplateSubstitution }
  }
  val defaultVersionsNavigationAdder by extending {
      dokkaBase.htmlPreprocessors providing ::VersionsNavigationAdder order {
          after(dokkaBase.assetsInstaller)
      }
  }
}