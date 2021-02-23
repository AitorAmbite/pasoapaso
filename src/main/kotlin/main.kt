import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// ojo con las corrutinas, tenemos que tocar el gradle... es una mofa asike por sea caso tenlo presente

//todo esto es los materiales que necesito para poder irme en la barca, se irán sumando
//hasta llegar al tope, los topes están abajo.
var cubosActuales = 0
var lenaActual = 0
var ramasActuales = 0
var comidaActual = 0

const val CUBOS_NECESARIOS = 4
const val LENA_NECESARIA  = 2
const val RAMA_NECESARIA  = 1
const val COMIDA_NECESARIA  = 1

//Basicamente un Mutex() es un semáforo en el cual solo puede entrar un elemento, en este caso solo puede coger el hacha y la hamaca una a la vez
var hamaca = Mutex()
var hacha = Mutex()

fun main() {
    comenzar()
    Thread.sleep(80000)     // en este caso usamos sleep puesto que esta en el metodo pricipal y no es una corrutina, si fuera una corrutina seria delay
}

//Lo que haces realmente es crear un espacio en el cual realizas 3 corrutinas chiquititas,
fun comenzar(){
    GlobalScope.launch {
        coroutineScope {
            // Amigo A el cual se va a encargar
            //de ir a por cubos de agua y descansar siempre y cuando la cama esté libre, por eso hemos puesto antes lo del mutex, porque solo 1 la puede usar a la vez
            launch {
                repeat(CUBOS_NECESARIOS) {  //--->Hacemos un repeat, puesto que si sabemos cuantas veces tiene que ser realizada esta operación.
                    irAPorAgua("Amigo A")  //-->Llama a la función ir a por agua, en ella simplemente existe un delay de x segundos,
                    descansar(1000, "Amigo A") //--> y la vuelta intenta descansar, en este caso pasamos el tiempo de descanso y
                    // también pasamos el nombre de quien va a descansar, solamente podrá usarla si está libre, sino esperará a que este libre la hamaca
                }
            }
            // Amigo B es el enacrgado de coger leña, y para ello necesitaría el hacha, por ello lanzamos la segunda minicorrutina
            launch {
                repeat(LENA_NECESARIA){ //--->Hacemos un repeat, puesto que si sabemos cuantas veces tiene que ser realizada esta operación.
                    irAPorLena("Amigo B")  //Aquí iriamos a por leña, la cosa es que primero debe estar el hacha libre, sino esperará a que lo esté
                    descansar(3000, "Amigo B") //Igualk en cuanto al descanso del amigo b, hasta que no esté la hamaca libre esperará puesto
                    // que gracias al mutex solo puede ir un amigo a la vez
                }
            }
            // Amigo C  es el encargado de recoger ramas y también de cazar
            launch {
                irAPorRamas("Amigo C")  //-->Primero va a por ramas, no tiene nada más dentro del metodo puesto que simplemente tarda x segundos en realizarlo
                irACazar("Amigo C")  //A la hora de ir a cazar,el AMIGO C necesita el hacha, y eso solo lo podra realizar si previamente está libre el hacha
            }

        }
        //Una vez que se hayan realizado todas las corrutinas pequeñas se realizara la comprobación de si se ha realizado bien la recoleccion y si finalmente
        //pueden excapar de la isla
        if (cubosActuales == CUBOS_NECESARIOS && lenaActual == LENA_NECESARIA && ramasActuales == RAMA_NECESARIA && comidaActual == COMIDA_NECESARIA){
            println("Barca construida y aprovisionada con exito")
        } else {
            println("Algo ha fallado")
        }
    }
}

suspend fun irACazar(nombre : String) {  //Método vamos a cazar, basicamente lo que hacemos es lo siguiente.
    println("El amigo $nombre va a Cazar")
    hacha.withLock {  //--> Aquí es donde tendríamos lo bueno de utilizar el mutex(), puesto que con
        // el withLock lo que conseguimos es que solo entre uno a la vez,  si otro hace la misma petición
        // se quedará en espera hasta que este withLock haya concluido por lo que esperara a que culmine el otro mutex()
        println("El amigo $nombre coge el hacha")
        delay(4000)
        comidaActual++
        println("El amigo $nombre deja el hacha") //Una vez se ha realizado entonces el siguiente que estaba esperando
        // para entrar en este mutex(), podrá entrar y culminar lo que estaba intentando hacer
    }
    println("El amigo $nombre va a por leña")
}

suspend fun descansar(tiempo : Long, nombre : String) {
    println("El amigo $nombre, quiere descansar")
    hamaca.withLock {
        println("El amigo $nombre, se tumba en la hamaca")
        delay(tiempo)
        println("El amigo $nombre, se levanta de la hamaca")
    }
    println("El amigo $nombre, deja de descansar")
}

suspend fun irAPorLena(nombre : String) {
    println("El amigo $nombre va a por leña")
    hacha.withLock {
        println("El amigo $nombre coge el hacha")
        delay(5000)
        lenaActual++
        println("El amigo $nombre deja el hacha")
    }
    println("El amigo $nombre vuelve con la leña")
}

suspend fun irAPorRamas(nombre : String) {
    println("El amigo $nombre va a por ramas")
    delay(3000)
    ramasActuales++
    println("El amigo $nombre vuelve con ramas")
}

suspend fun irAPorAgua(nombre : String) {
    println("El amigo $nombre va a por un cubo de agua")
    delay(3000)
    cubosActuales++
    println("El amigo $nombre vuelve con un cubo de agua")
}
