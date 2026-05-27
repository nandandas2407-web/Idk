package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Moshi Dataclasses for Retrofit Integration
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @Json(name = "text") val text: String
)

data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "responseSchema") val responseSchema: Map<String, Any>? = null
)

data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

// Retrofit Interface
interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    // Direct text helper for AI chat, inline edit, explainers, debugging
    suspend fun getAiResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured. Falling back to mock instructions.")
            return@withContext getMockResponse(prompt)
        }

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.7f),
            systemInstruction = systemInstruction?.let { GeminiContent(parts = listOf(GeminiPart(text = it))) }
        )

        try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No response content received from Orin AI."
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call failed: ${e.message}", e)
            "Error contacting Orin AI Cloud. Details: ${e.localizedMessage}. Running locally in Sandbox offline."
        }
    }

    // Helper to generate full project files reactively
    suspend fun generateCompleteProjectFiles(
        appName: String,
        projectType: String,
        prompt: String
    ): Map<String, String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // High fidelity localized mock templates
            return@withContext generateFidelityMockProject(appName, projectType, prompt)
        }

        val systemPrompt = """
            You are Orin IDE's core AI, a world-class mobile software compiler.
            Generate a set of files for a $projectType app called "$appName" that does: $prompt.
            
            Return exclusively a JSON map containing files relative to the project root.
            Format:
            {
               "file_path_1": "code content 1",
               "file_path_2": "code content 2"
            }
            Do not include any extra markdown delimiters, just pure clean valid JSON.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = "Please generate the files for $appName")))),
            generationConfig = GeminiGenerationConfig(temperature = 0.5f, responseMimeType = "application/json"),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val response = api.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!jsonText.isNullOrEmpty()) {
                val adapter = moshi.adapter(Map::class.java)
                val rawMap = adapter.fromJson(jsonText) as? Map<*, *>
                val resultMap = mutableMapOf<String, String>()
                rawMap?.forEach { (k, v) ->
                    if (k is String && v is String) {
                        resultMap[k] = v
                    }
                }
                if (resultMap.isNotEmpty()) {
                    return@withContext resultMap
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini Project Generation failed: ${e.message}")
        }

        // Default layout fallback on error
        return@withContext generateFidelityMockProject(appName, projectType, prompt)
    }

    private fun getMockResponse(prompt: String): String {
        return when {
            prompt.contains("explain", ignoreCase = true) -> {
                """// Orin AI Code Explanation:
This file is the main layout sheet. It configures:
- Status bars and Material 3 edge-to-edge screens.
- Reactive `MutableStateFlow` bindings which update elements instantly.
- Click action listeners connecting UI input directly with safe thread workers.
"""
            }
            prompt.contains("refactor", ignoreCase = true) || prompt.contains("optimize", ignoreCase = true) -> {
                """// Orin AI Optimization:
We have updated and refactored the reactive thread listeners:
1. Replaced side effect operations with dedicated Coroutine scopes.
2. Memoized styling brushes inside remember blocks for zero unnecessary layouts.
3. Added strict type boundary checking.
"""
            }
            prompt.contains("fix", ignoreCase = true) || prompt.contains("debug", ignoreCase = true) -> {
                """// Orin AI Bug Analysis:
Issue resolved: The dynamic render thread was triggered before configuration insets were completed.
Fix: Tied layout measures directly to standard Scaffold Windowinsets. 
Build output is now stable and compiled.
"""
            }
            else -> {
                """Hello! I am Orin AI, your integrated mobile development copilot.
I am running locally in your sandbox since your Gemini API key is not configured.
To experience real web-scale code generation and active debugging, add your API key in the AI Studio Secrets panel. 

I can assist you to generate, build, and optimize applications. Ask me to:
- "Refactor state variables"
- "Explain compile logs"
- "Write a dynamic button UI component"
"""
            }
        }
    }

    // High fidelity template creator on mock
    private fun generateFidelityMockProject(
        appName: String,
        projectType: String,
        prompt: String
    ): Map<String, String> {
        val dateStr = "2026-05-25"
        val lowercaseRaw = appName.lowercase().replace(" ", "_")
        return when (projectType) {
            "Kotlin Android" -> mapOf(
                "app/src/main/AndroidManifest.xml" to """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.orin.$lowercaseRaw">
    <uses-permission android:name="android.permission.INTERNET" />
    <application
        android:label="$appName"
        android:theme="@style/Theme.CoreApp">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>""",
                "app/src/main/java/MainActivity.kt" to """package com.orin.$lowercaseRaw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Autogenerated AI-First App
 * Concept: $prompt
 * Powered by Orin IDE
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    MainAppLayout()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout() {
    var query by remember { mutableStateOf("") }
    val mockItems = listOf(
        "Premium Track Alpha",
        "Deep Lounge Beats",
        "Synthesizer Midnight",
        "Acoustic Flow Cascade",
        "Cosmic Ambient Wave"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$appName", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B0B))
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .padding(16.dp)
        ) {
            // Concept card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Concept Core", color = Color(0xFF8B5CF6), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$prompt", color = Color.White, fontSize = 14.sp)
                }
            }

            // Input query
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search or Filter items") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color(0xFF1E1E1E)
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Text("Generated Content Directory", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(mockItems.filter { it.contains(query, ignoreCase = true) }) { item ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color(0xFF0B0B0B)),
                        modifier = Modifier
                            .background(Color(0xFF0B0B0B))
                            .padding(8.dp),
                        headlineContent = { Text(item, color = Color.White, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Categorized in synthesis streams", color = Color.Gray, fontSize = 12.sp) },
                        trailingContent = {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))
                            ) {
                                Text("Play", color = Color(0xFF8B5CF6), fontSize = 11.sp)
                            }
                        }
                    )
                }
            }
        }
    }
}""",
                "app/build.gradle.kts" to """plugins { id("com.android.application") }"""
            )

            "React Native" -> mapOf(
                "App.tsx" to """import React, { useState } from 'react';
import { StyleSheet, Text, View, FlatList, TextInput, TouchableOpacity, SafeAreaView } from 'react-native';

/**
 * Autogenerated react-native synthesis
 * Concept: $prompt
 */
export default function App() {
  const [items, setItems] = useState([
    { id: '1', title: 'Interactive Deck A', desc: 'Audio and synthesis core controller' },
    { id: '2', title: 'Studio Stream Ambient', desc: 'Relaxing ambient and lo-fi mix' },
    { id: '3', title: 'Orin Code Stream', desc: 'Podcast on active development' },
  ]);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>$appName AI Hub</Text>
        <Text style={styles.promptText}>Concept: $prompt</Text>
      </View>
      <FlatList
        data={items}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.cardTitle}>{item.title}</Text>
            <Text style={styles.cardDesc}>{item.desc}</Text>
            <TouchableOpacity style={styles.btn}>
              <Text style={styles.btnText}>Trigger Stream</Text>
            </TouchableOpacity>
          </View>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#000' },
  header: { padding: 24, backgroundColor: '#0B0B0B', borderBottomWidth: 1, borderBottomColor: '#1E1E1E' },
  title: { color: '#fff', fontSize: 24, fontWeight: 'bold' },
  promptText: { color: '#8B5CF6', fontSize: 13, marginTop: 4 },
  card: { backgroundColor: '#111', margin: 16, padding: 20, borderRadius: 12, borderWidth: 1, borderColor: '#1E1E1E' },
  cardTitle: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
  cardDesc: { color: '#A1A1AA', fontSize: 14, marginTop: 4, marginBottom: 16 },
  btn: { backgroundColor: '#8B5CF6', padding: 10, borderRadius: 8, alignItems: 'center' },
  btnText: { color: '#fff', fontWeight: 'bold' }
});""",
                "package.json" to """{ "name": "$lowercaseRaw", "dependencies": { "react": "18.3.1", "react-native": "0.75.2" } }"""
            )

            "Flutter" -> mapOf(
                "lib/main.dart" to """import 'package:flutter/material.dart';

/**
 * Flutter AI Core
 * Prompt: $prompt
 */
void main() => runApp(const MaterialApp(home: AiHome(), debugShowCheckedModeBanner: false));

class AiHome extends StatelessWidget {
  const AiHome({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: const Text('$appName', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        backgroundColor: const Color(0xFF0B0B0B),
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: const Color(0xFF111111),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: const Color(0xFF1E1E1E)),
              ),
              child: Column(
                children: [
                   const Text('AI Built App Engine', style: TextStyle(color: Color(0xFF8B5CF6), fontSize: 18, fontWeight: FontWeight.bold)),
                   const SizedBox(height: 8),
                   Text('$prompt', textAlign: TextAlign.center, style: const TextStyle(color: Color(0xFFA1A1AA), fontSize: 14)),
                ],
              ),
            ),
            const SizedBox(height: 24),
            _buildRow('Live Session Master', 'Status: Active on stream listener'),
            _buildRow('System Engine Audio', 'Status: Localized loop buffer'),
          ],
        ),
      ),
    );
  }

  Widget _buildRow(String title, String desc) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(color: const Color(0xFF0B0B0B), borderRadius: BorderRadius.circular(8)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
          const SizedBox(height: 4),
          Text(desc, style: const TextStyle(color: Colors.grey, fontSize: 13)),
        ],
      ),
    );
  }
}"""
            )

            else -> mapOf( // Default Web HTML/CSS/JS
                "index.html" to """<!DOCTYPE html>
<html>
<head>
    <title>$appName</title>
    <style>
        body { background: #000; color: #fff; font-family: sans-serif; text-align: center; padding: 50px; }
        .box { background: #111; border: 1px solid #1e1e1e; border-radius: 12px; padding: 30px; display: inline-block; max-width: 450px; }
        h1 { color: #8B5CF6; }
        p { color: #a1a1aa; font-size: 14px; line-height: 1.6; }
    </style>
</head>
<body>
    <div class="box">
        <h1>$appName</h1>
        <p><strong>Refined AI Concept:</strong> $prompt</p>
        <p>This workspace represents a fully simulated production deck created securely by Orin AI algorithms.</p>
    </div>
</body>
</html>"""
            )
        }
    }
}
