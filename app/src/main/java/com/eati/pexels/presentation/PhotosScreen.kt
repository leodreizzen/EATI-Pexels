package com.eati.pexels.presentation

import android.content.res.Configuration
import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eati.pexels.R
import com.eati.pexels.domain.Photo
import com.eati.pexels.presentation.ui.theme.EATIPexelsTheme

@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()
    val selected by viewModel.selectedFlow.collectAsState()
    Photos(result, selected, viewModel::updateResults, viewModel::onImageClick)
}

@Composable
fun Photos(
    results: List<Photo>,
    selected: Int,
    updateResults: (String) -> Unit,
    onImageClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        SearchBar(updateResults)
        Spacer(modifier = Modifier.height(16.dp))
        ImageGrid(results, selected, onImageClick)
    }
}

@Composable
private fun ImageGrid(
    results: List<Photo>,
    selected: Int,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
        state = gridState
    ) {
        itemsIndexed(results, span = { index, _ ->
            if (index == selected)
                GridItemSpan(2)
            else
                GridItemSpan(1)
        }
        ){ index, element ->
            ImageCard(
                photo = element,
                selected = index == selected,
                onClick = {onImageClick(index)}
            )
        }
    }
    LaunchedEffect(selected) {
        if (selected != -1) {
            gridState.scrollToItem(selected, 1)
        }
    }
}

@Composable
fun ImageCard(
    photo: Photo,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.surface
) {
    Card(modifier = modifier.clickable (onClick = onClick),
        backgroundColor = backgroundColor)
    {
        Column(modifier = Modifier.let { if (selected) it.padding(16.dp) else it }) {
            AsyncImage(
                model = photo.sourceURL,
                contentDescription = photo.alt,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_placeholder),
                modifier = Modifier.fillMaxWidth()
                    .fillMaxWidth().let {
                        if (!selected)
                            it.aspectRatio(1.0f)
                        else it
                            .height(200.dp)
                            .background(Color(parseColor(photo.avgColor)).copy(alpha = 0.7f))
                    },
                contentScale = if (selected) ContentScale.Fit else ContentScale.Crop
            )
            if (selected) {
                PhotoInfo(photo, Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun PhotoInfo(photo: Photo, modifier: Modifier = Modifier) {
    Column(modifier){
        InfoText(
            title = stringResource(R.string.autor),
            text = photo.photographer,
            modifier = Modifier.padding(top = 4.dp)
        )
        InfoText(
            title = stringResource(R.string.descripcion),
            text = photo.alt,
            modifier = Modifier.padding(top = 4.dp)
        )
        URLText(
            title = stringResource(R.string.url_post),
            url = photo.url,
            modifier = Modifier.padding(top = 4.dp)
        )
        URLText(
            title = stringResource(R.string.url_perfil),
            url = photo.photographerUrl,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun URLText(title: String, url: String, modifier: Modifier = Modifier) {
    val handler = LocalUriHandler.current
    val textStyle =
        MaterialTheme.typography.body2.toSpanStyle().copy(color = MaterialTheme.colors.onBackground)
    val linkStyle = textStyle.copy(color = Color.Blue)
    val titleString = "$title: "

    val string = buildAnnotatedString {
        withStyle(textStyle.copy(fontWeight = FontWeight.Bold)) {
            append(titleString.replace(' ', '\u00A0'))
        }
        withStyle(linkStyle)
        {
            append(url.replace(' ', '\u00A0'))
        }
    }
    ClickableText(
        string,
        modifier = modifier,
        onClick = { if (it >= title.length) handler.openUri(url) })
}

@Composable
fun InfoText(title: String, text: String, modifier: Modifier = Modifier) {
    val textStyle =
        MaterialTheme.typography.body2.toSpanStyle().copy(color = MaterialTheme.colors.onBackground)
    val titleString = "$title: "

    val string = buildAnnotatedString {
        withStyle(textStyle.copy(fontWeight = FontWeight.Bold))
        {
            append(titleString)
        }
        withStyle(textStyle)
        {
            append(text)
        }
    }
    Text(string, modifier = modifier)
}

@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var value by remember { mutableStateOf("") }
    Row(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = { value = it },
            placeholder = { Text("Buscar") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED, showBackground = true)
@Composable
fun SearchBarPreview() {
    EATIPexelsTheme{
        SearchBar({})
    }
}

@Preview()
@Composable
fun SelectedImageCardPreview() {
    EATIPexelsTheme{
        Surface{
            ImageCard(photo = previewPhoto(), selected = true)
        }
    }
}

@Preview()
@Composable
fun ImageCardPreview() {
    EATIPexelsTheme{
        Surface{
            ImageCard(photo = previewPhoto() , selected = false)
        }
    }
}

fun previewPhoto(): Photo{
    return Photo(
        id = 3589903,
        width = 2250,
        height = 3000,
        url = "https://www.pexels.com/es-es/foto/persona-que-trabaja-en-la-computadora-3589903/",
        photographer = "Inga Seliverstova",
        photographerUrl = "https://www.pexels.com/es-es/@inga-sv/",
        photographerId = 0,
        avgColor = "#5E5E5C",
        liked = false,
        alt = "Persona Que Trabaja En La Computadora",
        sourceURL = "https://images.pexels.com/photos/3589903/pexels-photo-3589903.jpeg"
    )
}