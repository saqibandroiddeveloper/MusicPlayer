package com.example.musicplayer.di

import android.content.Context
import android.view.LayoutInflater
import com.example.musicplayer.databinding.ActivityAboutBinding
import com.example.musicplayer.databinding.ActivityFavouriteBinding
import com.example.musicplayer.databinding.ActivityFeedbackBinding
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.ActivitySettingBinding
import com.example.musicplayer.databinding.PlaylistDetailsBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object AppModule {

    @Provides
    @ActivityScoped
    fun getActivityMainBinding(@ActivityContext context: Context):ActivityMainBinding{
        return ActivityMainBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivityPlayerBinding(@ActivityContext context: Context):ActivityPlayerBinding{
        return ActivityPlayerBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivityPlaylistBinding(@ActivityContext context: Context):ActivityPlaylistBinding{
        return ActivityPlaylistBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivityFavouriteBinding(@ActivityContext context: Context):ActivityFavouriteBinding{
        return ActivityFavouriteBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivityPlaylistDetails(@ActivityContext context: Context):PlaylistDetailsBinding{
        return PlaylistDetailsBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivityFeedbackBinding(@ActivityContext context: Context):ActivityFeedbackBinding{
        return ActivityFeedbackBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivitySettingBinding(@ActivityContext context: Context):ActivitySettingBinding{
        return ActivitySettingBinding.inflate(LayoutInflater.from(context))
    }

    @Provides
    @ActivityScoped
    fun getActivityAboutBinding(@ActivityContext context: Context):ActivityAboutBinding{
        return ActivityAboutBinding.inflate(LayoutInflater.from(context))
    }

}