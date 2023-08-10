package org.example.detekt.util

import kotlin.collections.ArrayList


//class to store edges of the weighted graph
internal class Edge(var src: Int, var dest: Int, var weight: Int)

// Graph class
internal class Graph(edges: List<Edge>) {
    // node of adjacency list
    internal class Node(var value: Int, var weight: Int)

    // define adjacency list
    var adj_list = ArrayList<ArrayList<Node>>()

    //Graph Constructor
    init {
        // adjacency list memory allocation

        for (i in edges.indices) adj_list.add(i, ArrayList<Node>())

        // add edges to the graph
        for (e in edges) {
            // allocate new node in adjacency List from src to dest
            adj_list[e.src].add(Node(e.dest, e.weight))
        }
    }

    companion object {
        // print adjacency list for the graph
        fun printGraph(graph: Graph) {
            var srcVertex = 0
            val listSize = graph.adj_list.size
            println("The contents of the graph:")
            while (srcVertex < listSize) {
                //traverse through the adjacency list and print the edges
                for (edge in graph.adj_list[srcVertex]) {
                    print(
                        "Vertex:" + srcVertex + " ==> " + edge.value +
                                " (" + edge.weight + ")\t"
                    )
                }
                println()
                srcVertex++
            }
        }
    }
}
