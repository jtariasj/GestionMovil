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
import cl.duoc.gestionmovil.ui.dashboard.DashboardScreen
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

        // Ruta 2: Dashboard (Ahora recibe el rol correctamente)
        composable("dashboard/{rol}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"

            DashboardScreen(
                rolUsuario = rol,
                onNavigateToList = { navController.navigate("oportunidad_list/$rol") }, // Pasamos el rol a la lista
                onNavigateToForm = { navController.navigate("oportunidad_form") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Ruta 3: Listado de Oportunidades (Recibe el rol para pasarlo al detalle)
        composable("oportunidad_list/{rol}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"
            val listViewModel: OportunidadListViewModel = viewModel(
                factory = OportunidadListViewModelFactory(oportunidadDao)
            )
            cl.duoc.gestionmovil.ui.oportunidad.OportunidadListScreen(
                viewModel = listViewModel,
                onItemClick = { idSeleccionado ->
                    // Navegamos al detalle llevando el ID y el ROL
                    navController.navigate("oportunidad_detail/$idSeleccionado/$rol")
                }
            )
        }

        // Ruta 4: Formulario de Oportunidad
        composable("oportunidad_form") {
            val formViewModel: cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModel =
                viewModel(
                    factory = cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModelFactory(
                        oportunidadDao
                    )
                )

            cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormScreen(
                viewModel = formViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ruta 5: Detalle de la oportunidad (Ahora recibe el ID y el ROL)
        composable(
            route = "oportunidad_detail/{oportunidadId}/{rol}",
            arguments = listOf(
                androidx.navigation.navArgument("oportunidadId") {
                    type = androidx.navigation.NavType.IntType
                },
                androidx.navigation.navArgument("rol") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("oportunidadId") ?: 0
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"

            val detailViewModel: cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailViewModel =
                viewModel(
                    factory = cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailViewModelFactory(
                        oportunidadDao
                    )
                )

            cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailScreen(
                oportunidadId = id,
                rolUsuario = rol, // Le pasamos el rol a la pantalla
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}