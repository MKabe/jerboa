@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.person

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.PersonViewSafe
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.samplePersonView
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.PROFILE_BANNER_SIZE
import com.jerboa.ui.theme.muted

@Composable
fun PersonProfileTopSection(
    personView: PersonViewSafe,
    modifier: Modifier = Modifier,
) {
    Column {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart,
        ) {
            personView.person.banner?.also {
                PictrsBannerImage(
                    url = it,
                    modifier = Modifier.height(PROFILE_BANNER_SIZE),
                )
            }
            Box(modifier = Modifier.padding(MEDIUM_PADDING)) {
                personView.person.avatar?.also {
                    LargerCircularIcon(icon = it)
                }
            }
        }
        Column(
            modifier = Modifier.padding(MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
        ) {
            Text(
                text = personNameShown(personView.person, true),
                style = MaterialTheme.typography.titleLarge,
            )

            TimeAgo(
                precedingString = "Joined",
                includeAgo = true,
                published = personView.person.published,
            )
            CommentsAndPosts(personView)
            personView.person.bio?.also {
                MyMarkdownText(
                    markdown = it,
                    color = MaterialTheme.colorScheme.onBackground.muted,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
fun CommentsAndPosts(personView: PersonViewSafe) {
    Row {
        Text(
            text = "${personView.counts.post_count} posts",
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${personView.counts.comment_count} comments",
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
    }
}

@Preview
@Composable
fun CommentsAndPostsPreview() {
    CommentsAndPosts(personView = samplePersonView)
}

@Preview
@Composable
fun PersonProfileTopSectionPreview() {
    PersonProfileTopSection(personView = samplePersonView)
}

@Composable
fun PersonProfileHeader(
    personName: String,
    myProfile: Boolean,
    onClickSortType: (SortType) -> Unit,
    onBlockPersonClick: () -> Unit,
    onReportPersonClick: () -> Unit,
    selectedSortType: SortType,
    navController: NavController = rememberNavController(),
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showSortOptions) {
        SortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
            onClickSortTopOptions = {
                showSortOptions = false
                showTopOptions = !showTopOptions
            },
        )
    }

    if (showTopOptions) {
        SortTopOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            },
        )
    }

    if (showMoreOptions) {
        PersonProfileMoreDialog(
            onDismissRequest = { showMoreOptions = false },
            onBlockPersonClick = {
                showMoreOptions = false
                onBlockPersonClick()
            },
            onReportPersonClick = {
                showMoreOptions = false
                onReportPersonClick()
            },
        )
    }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            PersonProfileHeaderTitle(
                personName = personName,
                selectedSortType = selectedSortType,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = "TODO",
                )
            }
            if (!myProfile) {
                IconButton(onClick = {
                    showMoreOptions = !showMoreOptions
                }) {
                    Icon(
                        Icons.Outlined.MoreVert,
                        contentDescription = "TODO",
                    )
                }
            }
        },
    )
}

@Composable
fun PersonProfileHeaderTitle(
    personName: String,
    selectedSortType: SortType,
) {
    Column {
        Text(
            text = personName,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = selectedSortType.toString(),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun PersonProfileMoreDialog(
    onDismissRequest: () -> Unit,
    onBlockPersonClick: () -> Unit,
    onReportPersonClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Block Person",
                    icon = Icons.Outlined.Block,
                    onClick = onBlockPersonClick,
                )
                IconAndTextDrawerItem(
                    text = "Report Person",
                    icon = Icons.Outlined.Flag,
                    onClick = onReportPersonClick,
                )
            }
        },
        confirmButton = {},
    )
}
