package com.example.nenaai.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nenaai.common.composables.ImagePlaceholder
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onOnboardingFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to NENA AI",
            description = "Your personal guide to financial empowerment. We help you understand and access loans safely."
        ),
        OnboardingPage(
            title = "AI-Powered Assistance",
            description = "Get clear, simple answers about loan products in your own language. No confusing jargon."
        ),
        OnboardingPage(
            title = "Secure & Trustworthy",
            description = "Your data is private and protected. Build your financial future with confidence."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            page ->
            OnboardingPageContent(page = pages[page])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DotsIndicator(pagerState = pagerState)

            Button(onClick = {
                if (pagerState.currentPage == pages.size - 1) {
                    onOnboardingFinished()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }) {
                Text(text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next")
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ImagePlaceholder()
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsIndicator(pagerState: PagerState) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pagerState.pageCount) { iteration ->
            val color = animateColorAsState(
                targetValue = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                label = "dotColor"
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color.value)
            )
        }
    }
}