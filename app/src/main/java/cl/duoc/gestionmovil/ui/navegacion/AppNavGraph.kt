package cl.duoc.gestionmovil.ui.navegacion

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.gestionmovil.data.*
import cl.duoc.gestionmovil.ui.contacto.ContactoScreen
import cl.duoc.gestionmovil.ui.contacto.ContactoViewModel
import cl.duoc.gestionmovil.ui.contacto.ContactoViewModelFactory
import cl.duoc.gestionmovil.ui.dashboard.DashboardScreen
import cl.duoc.gestionmovil.ui.login.LoginScreen
import cl.duoc.gestionmovil.ui.login.LoginViewModel
import cl.duoc.gestionmovil.ui.login.LoginViewModelFactory
import cl.duoc.gestionmovil.ui.oportunidad.*
import cl.duoc.gestionmovil.ui.organizacion.OrganizacionScreen
import cl.duoc.gestionmovil.ui.organizacion.OrganizacionViewModel
import cl.duoc.gestionmovil.ui.organizacion.OrganizacionViewModelFactory
import cl.duoc.gestionmovil.ui.seguimiento.SeguimientoScreen
import cl.duoc.gestionmovil.ui.seguimiento.SeguimientoViewModel
import cl.duoc.gestionmovil.ui.seguimiento.SeguimientoViewModelFactory

@Composable
fun AppNavGraph(
    usuarioDao: UsuarioDao,
    oportunidadDao: OportunidadDao,
    seguimientoDao: SeguimientoDao,
    organizacionDao: OrganizacionDao,
    contactoDao: ContactoDao
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinos.LOGIN,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // Ruta 1: Login
        composable(Destinos.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(usuarioDao)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { rol, nombre ->
                    navController.navigate("dashboard/$rol/$nombre") {
                        popUpTo(Destinos.LOGIN) { inclusive = true }
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
                onNavigateToList = { navController.navigate("oportunidad_list/$rol/$nombre") },
                onNavigateToForm = { navController.navigate(Destinos.NUEVA_OPORTUNIDAD) },
                onNavigateToOrganizaciones = { navController.navigate(Destinos.ORGANIZACIONES) },
                onLogout = {
                    navController.navigate(Destinos.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Ruta 3: Listado de Oportunidades
        composable("oportunidad_list/{rol}/{nombre}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""

            val listViewModel: OportunidadListViewModel = viewModel(
                factory = OportunidadListViewModelFactory(oportunidadDao, rol, nombre)
            )
            OportunidadListScreen(
                viewModel = listViewModel,
                onItemClick = { idSeleccionado ->
                    navController.navigate("oportunidad_detail/$idSeleccionado/$rol")
                }
            )
        }

        // Ruta 4: Formulario de Oportunidad
        composable(Destinos.NUEVA_OPORTUNIDAD) {
            val formViewModel: OportunidadFormViewModel = viewModel(
                factory = OportunidadFormViewModelFactory(oportunidadDao)
            )
            OportunidadFormScreen(
                viewModel = formViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ruta 5: Detalle de Oportunidad
        composable(
            route = "oportunidad_detail/{oportunidadId}/{rol}",
            arguments = listOf(
                navArgument("oportunidadId") { type = NavType.IntType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("oportunidadId") ?: 0
            val rol = backStackEntry.arguments?.getString("rol") ?: "EJECUTIVO"

            val detailViewModel: OportunidadDetailViewModel = viewModel(
                factory = OportunidadDetailViewModelFactory(oportunidadDao)
            )
            OportunidadDetailScreen(
                oportunidadId = id,
                rolUsuario = rol,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() },
                onIrASeguimiento = { idOpt ->
                    navController.navigate(Destinos.seguimiento(idOpt))
                }
            )
        }

        // Ruta 6: Seguimientos
        composable(
            route = Destinos.SEGUIMIENTO,
            arguments = listOf(
                navArgument("oportunidadId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("oportunidadId") ?: 0

            val seguimientoViewModel: SeguimientoViewModel = viewModel(
                factory = SeguimientoViewModelFactory(seguimientoDao, id)
            )
            SeguimientoScreen(
                viewModel = seguimientoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ruta 7: Organizaciones
        composable(Destinos.ORGANIZACIONES) {
            val orgViewModel: OrganizacionViewModel = viewModel(
                factory = OrganizacionViewModelFactory(organizacionDao)
            )
            OrganizacionScreen(
                viewModel = orgViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToContactos = { orgId ->
                    navController.navigate("contactos/$orgId")
                }
            )
        }

        // Ruta 8: Contactos de Organización
        composable(
            route = "contactos/{orgId}",
            arguments = listOf(navArgument("orgId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orgId = backStackEntry.arguments?.getInt("orgId") ?: 0

            val contactoViewModel: ContactoViewModel = viewModel(
                factory = ContactoViewModelFactory(contactoDao, orgId)
            )
            ContactoScreen(
                viewModel = contactoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}