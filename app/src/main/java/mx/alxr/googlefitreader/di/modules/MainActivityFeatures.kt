package mx.alxr.googlefitreader.di.modules

import dagger.Binds
import dagger.Module
import mx.alxr.googlefitreader.repository.IGoogleFitWrapper
import mx.alxr.googlefitreader.repository.impl.GoogleFitWrapper
import mx.alxr.googlefitreader.utils.IResourcesWrapper
import mx.alxr.googlefitreader.utils.ResourcesWrapper

@Module
abstract class MainActivityFeatures {

    @Binds
    @MainActivityScope
    abstract fun bindGoogleFitWrapper(helper: GoogleFitWrapper): IGoogleFitWrapper

    @Binds
    @MainActivityScope
    abstract fun bindResourcesWrapper(wrapper: ResourcesWrapper): IResourcesWrapper

}