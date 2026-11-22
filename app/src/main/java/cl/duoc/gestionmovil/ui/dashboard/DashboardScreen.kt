package cl.duoc.gestionmovil.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.gestionmovil.ui.theme.AmarilloDorado
import cl.duoc.gestionmovil.ui.theme.AzulIndigo
import cl.duoc.gestionmovil.ui.theme.NaranjaAmbar
import cl.duoc.gestionmovil.ui.theme.TextoOscuro
import androidx.compose.runtime.remember
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.EstadoOportunidad
import cl.duoc.gestionmovil.model.Prioridad
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    rolUsuario: String?,
    nombreUsuario: String = "",
    oportunidadRepository: OportunidadRepository? = null,
    onNavigateToList: () -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToOrganizaciones: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val estadoIndicadores by viewModel.uiState.collectAsStateWithLifecycle()
    val esSupervisor = rolUsuario.equals("SUPERVISOR", ignoreCase = true)
    val todasLasOportunidades by remember(oportunidadRepository) {
        oportunidadRepository?.getAllOportunidades() ?: flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val oportunidadesVisibles by remember(oportunidadRepository, esSupervisor, nombreUsuario) {
        when {
            oportunidadRepository == null -> flowOf(emptyList())
            esSupervisor || nombreUsuario.isBlank() -> oportunidadRepository.getAllOportunidades()
            else -> oportunidadRepository.obtenerPorResponsable(nombreUsuario)
        }
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val activas = oportunidadesVisibles.filter {
        it.estado != EstadoOportunidad.PARTICIPACION_CONFIRMADA &&
                it.estado != EstadoOportunidad.DESCARTADA
    }
    val pendientesContacto = activas.count {
        it.estado == EstadoOportunidad.EVENTO_IDENTIFICADO ||
                it.estado == EstadoOportunidad.CONTACTO_PENDIENTE
    }
    val enSeguimiento = activas.count {
        it.estado == EstadoOportunidad.CONTACTO_REALIZADO ||
                it.estado == EstadoOportunidad.PROPUESTA_ENVIADA ||
                it.estado == EstadoOportunidad.EN_ESPERA_RESPUESTA
    }
    val prioridadAlta = activas.count { it.prioridad == Prioridad.ALTA }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión Móvil",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AmarilloDorado,
                    titleContentColor = TextoOscuro,
                    actionIconContentColor = TextoOscuro
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner Principal (Azul Índigo + Acento Amarillo como la referencia)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AzulIndigo),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AmarilloDorado),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TextoOscuro,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Bienvenido",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Perfil: ${rolUsuario ?: "EJECUTIVO"}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AmarilloDorado,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Indicadores de APIs Externas (Dólar + Clima)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = AmarilloDorado,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Información del Día",
                            style = MaterialTheme.typography.labelLarge,
                            color = AmarilloDorado,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    when (val actual = estadoIndicadores) {
                        is IndicadoresUiState.Cargando -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = AmarilloDorado
                                )
                                Text(
                                    "Consultando Dólar y Clima...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }

                        is IndicadoresUiState.Error -> {
                            Text(
                                text = actual.mensaje,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFFD5D5)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.cargarIndicadores() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmarilloDorado,
                                    contentColor = TextoOscuro
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Reintentar", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        is IndicadoresUiState.Exito -> {
                            Text(
                                text = "• Valor Dólar hoy: $${actual.valorDolar} CLP",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = "• Clima Santiago: ${actual.temperatura}°C (${actual.descripcionClima}, viento ${actual.viento} km/h)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }

            // Sección de Accesos Rápidos (Tarjetas blancas con íconos en recuadro de color)
            Text(
                text = if (esSupervisor) "Panel de Control (Supervisor)" else "Mis Gestiones (Ejecutivo)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccesoRapidoCard(
                    titulo = "Nueva\nOportunidad",
                    icono = Icons.Default.AddCircle,
                    colorIconoFondo = Color(0xFF3F51B5),
                    onClick = onNavigateToForm,
                    modifier = Modifier.weight(1f)
                )
                AccesoRapidoCard(
                    titulo = if (esSupervisor) "Todas las\nOportunidades" else "Mis\nOportunidades",
                    icono = Icons.AutoMirrored.Filled.ListAlt,
                    colorIconoFondo = NaranjaAmbar,
                    onClick = onNavigateToList,
                    modifier = Modifier.weight(1f)
                )
                AccesoRapidoCard(
                    titulo = "Directorio\nEmpresas",
                    icono = Icons.Default.Business,
                    colorIconoFondo = Color(0xFF10B981),
                    onClick = onNavigateToOrganizaciones,
                    modifier = Modifier.weight(1f)
                )
            }

            // Tarjeta Blanca Elevada de Alertas y Pendientes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Alertas y Pendientes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (activas.isEmpty()) {
                        Text(
                            "• Sin alertas ni gestiones pendientes por ahora.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "• $pendientesContacto oportunidades pendientes de contacto inicial",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• $enSeguimiento oportunidades en gestión o espera de respuesta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• $prioridadAlta gestiones activas con Prioridad ALTA",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (prioridadAlta > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón secundario de cierre de sesión
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Cerrar Sesión", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AccesoRapidoCard(
    titulo: String,
    icono: ImageVector,
    colorIconoFondo: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorIconoFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                minLines = 2
            )
        }
    }
}