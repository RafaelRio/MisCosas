package com.rafario.miscosas.presentation.on_boarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafario.miscosas.R
import com.rafario.miscosas.ui.components.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun OnBoardingCarousel(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    onSkip: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    val scope = rememberCoroutineScope()


    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        OnboardingPage(
            page = page,
            currentPage = pagerState.currentPage,
            onNext = {
                if (page < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(page + 1)
                    }
                } else {
                    onFinish()
                }
            },
            onSkip = onSkip
        )
    }


}

@Composable
fun OnboardingPage(
    page: Int,
    currentPage: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val data = when (page) {
        0 -> OnboardingData(
            emoji = "📦",
            title = stringResource(R.string.onboarding_step_one_title),
            description = stringResource(R.string.onboarding_step_one_description),
            buttonText = stringResource(R.string.next)
        )

        1 -> OnboardingData(
            emoji = "🛡️",
            title = stringResource(R.string.onboarding_step_two_title),
            description = stringResource(R.string.onboarding_step_two_description),
            buttonText = stringResource(R.string.next)
        )

        else -> OnboardingData(
            emoji = "🏠",
            title = stringResource(R.string.onboarding_step_three_title),
            description = stringResource(R.string.onboarding_step_three_description),
            buttonText = stringResource(R.string.start)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.skip),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-80).dp)
                .size(170.dp)
                .background(
                    color = Color(0xFF173768),
                    shape = RoundedCornerShape(48.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.emoji,
                fontSize = 64.sp
            )
        }

        OnboardingBottomPanel(
            data = data,
            currentPage = currentPage,
            onNext = onNext,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun OnboardingBottomPanel(
    data: OnboardingData,
    currentPage: Int,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = 32.dp,
                    topEnd = 32.dp
                )
            )
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        OnboardingIndicator(
            currentPage = currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = data.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1f))

        PrimaryButton(
            text = data.buttonText,
            onClick = onNext
        )
    }
}

@Composable
fun OnboardingIndicator(
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->

            val selected = currentPage == index

            val width by animateDpAsState(
                targetValue = if (selected) 24.dp else 8.dp,
                animationSpec = tween(300),
                label = "indicatorWidth"
            )

            val color by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                animationSpec = tween(300),
                label = "indicatorColor"
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}

data class OnboardingData(
    val emoji: String,
    val title: String,
    val description: String,
    val buttonText: String
)