package jzam.arcedex

import android.app.Application
import jzam.arcedex.data.PokeResearchDatabase
import jzam.arcedex.data.PokeResearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/*
Base class for maintaining global application state. To use, set "android:name" attribute in the
AndroidManifest.xml's <application> tag. This class is instantiated before any other class when the
process for the application/package is created.
 */
class ArcedexApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    //by lazy ensures these are only created when needed rather than on app startup
    val pokeResearchDatabase by lazy { PokeResearchDatabase.getDatabase(this, applicationScope) }
    val pokeResearchRepository by lazy { PokeResearchRepository(pokeResearchDatabase.pokeResearchDao()) }
}