package cl.duoc.gestionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.gestionmovil.data.AppDatabase
import cl.duoc.gestionmovil.ui.theme.GestionMovilTheme

import cl.duoc.gestionmovil.ui.oportunidad.OportunidadListViewModel
import cl.duoc.gestionmovil.ui.oportunidad.OportunidadListViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos la base de datos de Room
        val database = AppDatabase.getDatabase(applicationContext)
        val usuarioDao = database.usuarioDao()
        val oportunidadDao = database.oportunidadDao()

        setContent {
            GestionMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. Iniciamos el sistema de navegación
                    AppNavigation(usuarioDao = usuarioDao, oportunidadDao = oportunidadDao)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    usuarioDao: cl.duoc.gestionmovil.data.UsuarioDao,
    oportunidadDao: cl.duoc.gestionmovil.data.OportunidadDao // Agregamos el DAO de oportunidades
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Ruta 1: Login
        composable("login") {
            val loginViewModel: cl.duoc.gestionmovil.ui.login.LoginViewModel = viewModel(
                factory = cl.duoc.gestionmovil.ui.login.LoginViewModelFactory(usuarioDao)
            )

            cl.duoc.gestionmovil.ui.login.LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { rol ->
                    navController.navigate("dashboard/${rol.name}") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Ruta 2: Dashboard
        composable("dashboard/{rolUsuario}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rolUsuario")
            cl.duoc.gestionmovil.ui.dashboard.DashboardScreen(
                rolUsuario = rol,
                onNavigateToList = { navController.navigate("oportunidad_list") },
                onNavigateToForm = { navController.navigate("oportunidad_form") } // <-- Cambio aquí
            )
        }

        // Ruta 3: Listado de Oportunidades
        composable("oportunidad_list") {
            val listViewModel: OportunidadListViewModel = viewModel(
                factory = OportunidadListViewModelFactory(oportunidadDao)
            )
            cl.duoc.gestionmovil.ui.oportunidad.OportunidadListScreen(
                viewModel = listViewModel,
                // ... tus otros parámetros ...
                onItemClick = { idSeleccionado ->
                    // Esto hace que viaje a la ruta 5 que creamos antes
                    navController.navigate("oportunidad_detail/$idSeleccionado")
                }
            )
        }

        // Ruta 4: Formulario de Oportunidad
        composable("oportunidad_form") {
            val formViewModel: cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModel = viewModel(
                factory = cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModelFactory(oportunidadDao)
            )

            cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormScreen(
                viewModel = formViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ruta 5: Detalle de la oportunidad
        composable(
            route = "oportunidad_detail/{oportunidadId}",
            arguments = listOf(androidx.navigation.navArgument("oportunidadId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("oportunidadId") ?: 0

            val detailViewModel: cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailViewModel = viewModel(
                factory = cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailViewModelFactory(oportunidadDao)
            )

            cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailScreen(
                oportunidadId = id,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}