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
import cl.duoc.gestionmovil.ui.Login.LoginScreen
import cl.duoc.gestionmovil.ui.Login.LoginViewModel
import cl.duoc.gestionmovil.ui.Login.LoginViewModelFactory
import cl.duoc.gestionmovil.ui.theme.GestionMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos la base de datos de Room
        val database = AppDatabase.getDatabase(applicationContext)
        val usuarioDao = database.usuarioDao()

        setContent {
            GestionMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. Iniciamos el sistema de navegación
                    AppNavigation(usuarioDao = usuarioDao)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(usuarioDao: cl.duoc.gestionmovil.data.UsuarioDao) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Ruta 1: Pantalla de Login
        composable("login") {
            // Instanciamos el ViewModel usando nuestro Factory
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(usuarioDao)
            )

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { rol ->
                    // Cuando el login sea exitoso, navegaremos al Dashboard
                    // Pasamos el rol como argumento para mostrar vistas distintas
                    navController.navigate("dashboard/${rol.name}") {
                        // Evita que el usuario vuelva al login presionando "Atrás"
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Ruta 2: Dashboard
        composable("dashboard/{rolUsuario}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rolUsuario")
            // Ahora sí llamamos a la pantalla real:
            cl.duoc.gestionmovil.ui.dashboard.DashboardScreen(rolUsuario = rol)
        }
    }
}