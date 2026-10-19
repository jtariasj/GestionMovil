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
        val seguimientoDao = database.seguimientoDao()
        val organizacionDao = database.organizacionDao()
        val contactoDao = database.contactoDao()

        setContent {
            GestionMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. Iniciamos el sistema de navegación
                    AppNavigation(usuarioDao = usuarioDao, oportunidadDao = oportunidadDao, seguimientoDao = seguimientoDao, organizacionDao = organizacionDao, contactoDao = contactoDao)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    usuarioDao: cl.duoc.gestionmovil.data.UsuarioDao,
    oportunidadDao: cl.duoc.gestionmovil.data.OportunidadDao,
    seguimientoDao: cl.duoc.gestionmovil.data.SeguimientoDao,
    organizacionDao: cl.duoc.gestionmovil.data.OrganizacionDao,
    contactoDao: cl.duoc.gestionmovil.data.ContactoDao
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
                onLoginSuccess = { rol, nombre ->
                    // Viajamos al dashboard llevando rol y nombre
                    navController.navigate("dashboard/$rol/$nombre") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Ruta 2: Dashboard
        composable("dashboard/{rol}/{nombre}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""

            DashboardScreen(
                rolUsuario = rol,
                // Pasamos rol y nombre a la ruta de la lista
                onNavigateToList = { navController.navigate("oportunidad_list/$rol/$nombre") },
                onNavigateToForm = { navController.navigate("oportunidad_form") },
                onNavigateToOrganizaciones = { navController.navigate("organizaciones") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Ruta 3: Listado de Oportunidades
        composable("oportunidad_list/{rol}/{nombre}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""

            // Le pasamos datos al Factory
            val listViewModel: OportunidadListViewModel = viewModel(
                factory = OportunidadListViewModelFactory(oportunidadDao, rol, nombre)
            )

            cl.duoc.gestionmovil.ui.oportunidad.OportunidadListScreen(
                viewModel = listViewModel,
                onItemClick = { idSeleccionado ->
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
                onNavigateBack = { navController.popBackStack() },
                // ESTA ES LA LÍNEA QUE FALTA AGREGAR:
                onIrASeguimiento = { idOpt ->
                    navController.navigate("seguimiento/$idOpt")
                }
            )
        }

        // Ruta 6: Seguimientos
        composable(
            route = "seguimiento/{oportunidadId}",
            arguments = listOf(
                androidx.navigation.navArgument("oportunidadId") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("oportunidadId") ?: 0

            val seguimientoViewModel: cl.duoc.gestionmovil.ui.seguimiento.SeguimientoViewModel = viewModel(
                factory = cl.duoc.gestionmovil.ui.seguimiento.SeguimientoViewModelFactory(seguimientoDao, id)
            )

            cl.duoc.gestionmovil.ui.seguimiento.SeguimientoScreen(
                viewModel = seguimientoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ruta 7: Organizaciones
        composable("organizaciones") {
            val orgViewModel: cl.duoc.gestionmovil.ui.organizacion.OrganizacionViewModel = viewModel(
                factory = cl.duoc.gestionmovil.ui.organizacion.OrganizacionViewModelFactory(organizacionDao)
            )

            cl.duoc.gestionmovil.ui.organizacion.OrganizacionScreen(
                viewModel = orgViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToContactos = { orgId ->
                    navController.navigate("contactos/$orgId") // Nos prepararemos para la pantalla 8
                }
            )
        }

        // Ruta 8: Contactos de Organización
        composable(
            route = "contactos/{orgId}",
            arguments = listOf(androidx.navigation.navArgument("orgId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val orgId = backStackEntry.arguments?.getInt("orgId") ?: 0

            val contactoViewModel: cl.duoc.gestionmovil.ui.contacto.ContactoViewModel = viewModel(
                factory = cl.duoc.gestionmovil.ui.contacto.ContactoViewModelFactory(contactoDao, orgId)
            )

            cl.duoc.gestionmovil.ui.contacto.ContactoScreen(
                viewModel = contactoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}