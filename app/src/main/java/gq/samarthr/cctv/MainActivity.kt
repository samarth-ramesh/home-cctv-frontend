package gq.samarthr.cctv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import gq.samarthr.cctv.ui.theme.CCTVGottigereTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCTVGottigereTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainViewer("Android")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewer(name: String, modifier: Modifier = Modifier) {
    VideoPlayer()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CCTVGottigereTheme {
        MainViewer("Android")
    }
}

@androidx.annotation.OptIn(UnstableApi::class) @SuppressLint("OpaqueUnitKey")
@Composable
fun VideoPlayer(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // create our player
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(MediaItem.fromUri("http://chidambaram:8080/6vXo7fFRN3XhBeF5L033yrtgIY8Ymb/hls/mTIPZzBAkJ/Back/s.m3u8"))
            this.prepare()
        }
    }

    ConstraintLayout(modifier = modifier) {
        val (title, videoPlayer) = createRefs()

        // video title
        Text(
            text = "Current Title",
            color = Color.White,
            modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // player view
        DisposableEffect(
            AndroidView(
                modifier =
                Modifier
                    .testTag("VideoPlayer")
                    .constrainAs(videoPlayer) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                factory = {

                    // exo player view for our video player
                    PlayerView(context).apply {
                        player = exoPlayer
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT
                            )
                    }
                }
            )
        ) {
            onDispose {
                // relase player when no longer needed
                exoPlayer.release()
            }
        }
    }
}