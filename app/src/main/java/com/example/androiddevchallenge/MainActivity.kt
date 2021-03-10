/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.Counter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

private const val ONE_MINUTE_IN_MILLIS = 60000L
private const val COUNT_DOWN_INTERVAL = 1000L

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        MainContent()
    }
}

@ExperimentalAnimationApi
@Composable
fun MainContent() {
    val progress = remember { mutableStateOf(1.0f) }
    val buttonState = remember { mutableStateOf("Start") }
    val textState = remember { mutableStateOf("60") }
    val visible = remember { mutableStateOf(true) }

    val countDownTimer =
        Counter(
            millisInFuture = ONE_MINUTE_IN_MILLIS, countDownInterval = COUNT_DOWN_INTERVAL,
            {
                // On Tick
                progress.value = it.toFloat() / ONE_MINUTE_IN_MILLIS.toFloat()

                textState.value = TimeUnit.MILLISECONDS.toSeconds(it).toString()
            },
            {
                // On Finish
                progress.value = 1.0f
                buttonState.value = "Start"
                textState.value = "60"
                visible.value = true
            }
        )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MyCircularProgressIndicator(progress.value, textState.value)
        Spacer(Modifier.height(30.dp))
        AnimatedVisibility(visible = visible.value) {
            CounterStateButton(buttonState.value) {
                if (buttonState.value == "Start") {
                    countDownTimer.start()
                    visible.value = false
                } else {
                    visible.value = true
                }
                buttonState.value = it
            }
        }
    }
}

@Composable
fun CounterStateButton(buttonTextState: String, onClickButton: (String) -> Unit) {
    OutlinedButton(
        onClick = {
            onClickButton(if (buttonTextState == "Start") "Stop" else "Start")
        }
    ) {
        Text(buttonTextState)
    }
}

@Composable
fun MyCircularProgressIndicator(progress: Float, timerTextState: String) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
    ) {

        CircularProgressIndicatorBackGround(
            modifier = Modifier
                .height(350.dp)
                .width(350.dp),
            color = colorResource(R.color.purple_500),
            stroke = 7
        )

        CircularProgressIndicator(
            modifier = Modifier
                .height(350.dp)
                .width(350.dp),
            progress = animatedProgress,
            color = colorResource(R.color.light_grey),
            strokeWidth = 7.dp
        )

        Text(
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp
            ),
            text = timerTextState
        )
    }
}

@Composable
fun CircularProgressIndicatorBackGround(
    modifier: Modifier = Modifier,
    color: Color,
    stroke: Int
) {
    val style = with(LocalDensity.current) { Stroke(stroke.dp.toPx()) }

    Canvas(modifier = modifier, onDraw = {

        val innerRadius = (size.minDimension - style.width)/2

        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 360f,
            topLeft = Offset(
                (size / 2.0f).width - innerRadius,
                (size / 2.0f).height - innerRadius
            ),
            size = Size(innerRadius * 2, innerRadius * 2),
            useCenter = false,
            style = style
        )

    })
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}


@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
