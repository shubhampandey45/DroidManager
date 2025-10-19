package com.sp45.androidmanager.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.google.gson.Gson
import com.sp45.androidmanager.domain.repository.SystemStatsRepository
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import java.io.File

data class SystemStatsInfo(
    val cpuProgress: Float = 0.0f,
    val memoryProgress: Float = 0.0f,
    val batteryProgress: Float = 0.0f,
    val storageProgress: Float = 0.0f,
    val networkValue: String = "--"
)

object SystemStatsSerializer : Serializer<SystemStatsInfo> {
    private val gson = Gson()
    override val defaultValue: SystemStatsInfo = SystemStatsInfo()

    override suspend fun readFrom(input: InputStream): SystemStatsInfo {
        return gson.fromJson(input.reader(), SystemStatsInfo::class.java)
    }

    override suspend fun writeTo(t: SystemStatsInfo, output: OutputStream) {
        output.writer().use {
            gson.toJson(t, it)
        }
    }
}

object SystemStatsStateDefinition : GlanceStateDefinition<SystemStatsInfo> {
    private const val DATA_STORE_FILE_NAME = "system_stats_widget"
    private val Context.systemStatsDataStore by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = SystemStatsSerializer
    )

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<SystemStatsInfo> {
        return context.systemStatsDataStore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILE_NAME)
    }
}

class SystemStatsWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<SystemStatsInfo> = SystemStatsStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Content(currentState())
            }
        }
    }

    @Composable
    fun Content(stats: SystemStatsInfo) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.Vertical.Top,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Text("System Stats", style = TextStyle(color = GlanceTheme.colors.onSurface))
            Spacer(modifier = GlanceModifier.height(16.dp))
            StatRow(label = "CPU", progress = stats.cpuProgress)
            Spacer(modifier = GlanceModifier.height(8.dp))
            StatRow(label = "Memory", progress = stats.memoryProgress)
            Spacer(modifier = GlanceModifier.height(8.dp))
            StatRow(label = "Battery", progress = stats.batteryProgress)
            Spacer(modifier = GlanceModifier.height(8.dp))
            StatRow(label = "Storage", progress = stats.storageProgress)
            Spacer(modifier = GlanceModifier.height(8.dp))
            ValueRow(label = "Net Rx", value = stats.networkValue)
        }
    }

    @Composable
    private fun StatRow(label: String, progress: Float) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = label,
                modifier = GlanceModifier.width(64.dp),
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                modifier = GlanceModifier.padding(start = 8.dp),
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
        }
    }

    @Composable
    private fun ValueRow(label: String, value: String) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = label,
                modifier = GlanceModifier.width(64.dp),
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
            Text(
                text = value,
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
        }
    }
}

@AndroidEntryPoint
class SystemStatsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SystemStatsWidget()

    @Inject
    lateinit var repository: SystemStatsRepository

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var dataCollectionJob: Job? = null

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        if (dataCollectionJob?.isActive == true) return

        dataCollectionJob = coroutineScope.launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            repository.getLatestStatsFlow().collect { entity ->
                entity?.let {
                    val info = SystemStatsInfo(
                        cpuProgress = (it.cpuSystemLoad / 100.0).toFloat(),
                        memoryProgress = (it.memUsedMB.toFloat() / it.memTotalMB.toFloat()),
                        batteryProgress = (it.batteryLevelPct / 100.0).toFloat(),
                        storageProgress = (1.0f - (it.storageInternalFreeGB.toFloat() / it.storageInternalTotalGB.toFloat())),
                        networkValue = "${it.networkTotalRxMB} MB"
                    )

                    val glanceIds = glanceAppWidgetManager.getGlanceIds(SystemStatsWidget::class.java)
                    glanceIds.forEach { glanceId ->
                        updateAppWidgetState(context, SystemStatsStateDefinition, glanceId) { info }
                        glanceAppWidget.update(context, glanceId)
                    }
                }
            }
        }
    }

    override fun onDisabled(context: Context) {
        dataCollectionJob?.cancel()
        dataCollectionJob = null
        super.onDisabled(context)
    }
}
