package cl.duoc.gestionmovil.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    rolUsuario: String?,
    onNavigateToList: () -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToOrganizaciones: () -> Unit, // 1. NUEVO PARAMETRO
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Perfil: $rolUsuario",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (rolUsuario == "SUPERVISOR") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Panel de Control (Supervisor)", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNavigateToList, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver todas las oportunidades")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mis Gestiones (Ejecutivo)", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNavigateToForm, modifier = Modifier.fillMaxWidth()) {
                            Text("Registrar nueva oportunidad")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNavigateToList, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver mis oportunidades")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN PARA ORGANIZACIONES
            Button(
                onClick = onNavigateToOrganizaciones,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Directorio de Organizaciones")
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(onClick = onLogout) {
                Text("Cerrar Sesión")
            }
        }
    }
}