package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.R
import com.example.huerto_hogar_aplicacion.ui.theme.CafeSombraTexto
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2000L)
        navController.popBackStack()
        navController.navigate("home")
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Huerto Hogar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    shadow = Shadow(
                        color = CafeSombraTexto,
                        offset = Offset(x = 2f, y = 2f),
                        blurRadius = 4f
                    )
                )
            )

            // Espaciador
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.pixelated_farm_definitive),
                contentDescription = "Logo de Huerto Hogar",
                //  contentScale para rellenar el borde
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(250.dp)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    // El clip es para que el contenido de la imagen
                    // se recorte con las esquinas redondeadas.
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}

