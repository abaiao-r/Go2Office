package com.example.go2office.di
import com.example.go2office.data.repository.OfficeRepositoryImpl
import com.example.go2office.domain.repository.OfficeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindOfficeRepository(
        officeRepositoryImpl: OfficeRepositoryImpl
    ): OfficeRepository
}
