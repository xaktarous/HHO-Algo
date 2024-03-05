import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

//LES TACHES
class Task {
    int id;
    int duration;

    Task(int id, int duration) {
        this.id = id;
        this.duration = duration;
    }
}

//LES RESOURCES
class Server {
    String id;
    List<Task> taskCurrent;

    Server(String id) {
        this.id = id;
        this.taskCurrent = new ArrayList<>();
    }
}

public class Main {
    
	 static final int popSize = 10; //POPULATION SIZE
	    static final int iterations = 100; // NOMBRE MAXIMALE D'ITERATIONS
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  List<Task> tasks = initializeTasks();
	        List<Server> servers = initializeServers();

	        Object[] arr = harrisHawksOptimizationFunc(popSize, servers, tasks, iterations); 

	        System.out.println("Meilleur ordonnancement trouvé");
	        printSolution((List<Server>) arr[1]);
	        System.out.println("Meilleure solution : " + arr[0]);
	}

      // INITIALIZER LES TACHES
    static List<Task> initializeTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, 10));
        tasks.add(new Task(2, 8));
        tasks.add(new Task(3, 7));
        return tasks;
    }
      // INITIALIZER LES SERVEURS
    static List<Server> initializeServers() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server("A"));
        servers.add(new Server("B"));
        return servers;
    }
      // METHOD DE GENERATION DES SOLUTIONS ALEATOIRE
    static List<Server> randomSol(List<Server> servers, List<Task> tasks) {
	    //reset
        List<Server> solution = new ArrayList<>();
        for (Server server : servers) {
            Server copyServer = new Server(server.id);
            copyServer.taskCurrent = new ArrayList<>();
            solution.add(copyServer);
        }
        Random random = new Random();
        for (Task task : tasks) {
            int serverIndex = random.nextInt(servers.size());
            solution.get(serverIndex).taskCurrent.add(task);
        }
        return solution;
    }
     
     // METHOD OBJECTIVE (CALCULER COMPLETION TIME)
    static int objectiveFunc(List<Server> servers) {
        List<Integer> times = new ArrayList<>();
        for (Server server : servers) {
            int executionTime = 0;
            int completionTime = 0;
            int numTasks = server.taskCurrent.size();
            if (numTasks == 0) continue;
            for (Task task : server.taskCurrent) {
                executionTime += task.duration;
                completionTime += executionTime;
            }
            completionTime /= numTasks;
            times.add(completionTime);
        }
        return max(times);
    }
   // METHOD MAX() 
    static int max(List<Integer> times) {
        int max = 0;
        for (int time : times) {
            if (time > max) {
                max = time;
            }
        }
        return max;
    }
     // METHOD UPDATE (ECHANGER LES TACHES ENTRE  DEUX SERVEURS)
    static List<Server> update(List<Server> servers) {
        // Cloner la liste de serveurs
        List<Server> newSolution = new ArrayList<>();
        for (Server server : servers) {
            Server newServer = new Server(server.id);
            newServer.taskCurrent = new ArrayList<>(server.taskCurrent); // Cloner la liste des tâches courantes
            newSolution.add(newServer);
        }

        // Choisir deux serveurs aléatoirement
        Random random = new Random();
        int server1Index = random.nextInt(newSolution.size());
        int server2Index;
        do {
            server2Index = random.nextInt(newSolution.size());
        } while (server2Index == server1Index); // selectioner deux different serveur

        Server server1 = newSolution.get(server1Index);
        Server server2 = newSolution.get(server2Index);

        // echanger les taches entre deux serveurs
        if (!server1.taskCurrent.isEmpty() && !server2.taskCurrent.isEmpty()) {
            int task1Index = random.nextInt(server1.taskCurrent.size());
            int task2Index = random.nextInt(server2.taskCurrent.size());

            Task task1 = server1.taskCurrent.get(task1Index);
            Task task2 = server2.taskCurrent.get(task2Index);

            // echanger les taches
            server1.taskCurrent.set(task1Index, task2);
            server2.taskCurrent.set(task2Index, task1);
        }

        return newSolution;
    }
     // LA METHOD D'ALGORITHME
    static Object[] harrisHawksOptimizationFunc(int size, List<Server> servers, List<Task> tasks, int iterations) {
        int bestCompletion = Integer.MAX_VALUE;
        List<Server> bestSolution = null; 
        // initialization
        List<List<Server>> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Server> solution = randomSol(servers, tasks);
            population.add(solution);
        }

        for (int iter = 0; iter < iterations; iter++) {
		 //exploration
            for (int i = 0; i < size; i++) {
		// generate random sol
                List<Server> randomSolution = randomSol(servers, tasks);
		    //evaluation
                int randomSolutionFitness = objectiveFunc(randomSolution);
		    // select
                if (randomSolutionFitness < bestCompletion) {
                    bestCompletion = randomSolutionFitness;
                    bestSolution = randomSolution;
                }
            }
            
            // exploitation
            population.sort((a, b) -> objectiveFunc(a) - objectiveFunc(b));

            // update la population
            for (int i = 0; i < size; i++) {
                population.set(i, update(population.get(i)));
            }
            // condition d'arrêt
            if (objectiveFunc(population.get(0)) <= bestCompletion) {
                bestCompletion = objectiveFunc(population.get(0));
                bestSolution = population.get(0);
                break;
            }
        }
        // solution truvée
        return new Object[]{bestCompletion, bestSolution};
    }
    // AFICHAGE DE LA SOLUTION (L'ORDONNANCEMENT CHOISI)
    static void printSolution(List<Server> servers) {
        for (Server server : servers) {
            System.out.println("Serveur " + server.id + ": ");
            for (Task task : server.taskCurrent) {
                System.out.println("Tâche " + task.id + ", Durée: " + task.duration);
            }
        }
    }

}
