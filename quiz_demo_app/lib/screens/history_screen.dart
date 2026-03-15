import 'package:flutter/material.dart';

import '../data/history_data.dart';

class HistoryScreen extends StatelessWidget {

  const HistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        title: const Text("Quiz History"),
        centerTitle: true,
      ),

      body: quizHistory.isEmpty
          ? const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [

                  Icon(
                    Icons.history,
                    size: 80,
                    color: Colors.grey,
                  ),

                  SizedBox(height: 15),

                  Text(
                    "No Quiz History Yet",
                    style: TextStyle(
                      fontSize: 18,
                      color: Colors.grey,
                    ),
                  )

                ],
              ),
            )

          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: quizHistory.length,

              itemBuilder: (context, index) {

                final item = quizHistory[index];

                double percent =
                    (item["score"] / item["total"]) * 100;

                return Container(
                  margin: const EdgeInsets.only(bottom: 15),

                  child: Card(
                    elevation: 4,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),

                    child: Padding(
                      padding: const EdgeInsets.all(16),

                      child: Row(
                        children: [

                          /// Score Circle
                          Container(
                            height: 60,
                            width: 60,

                            decoration: BoxDecoration(
                              color: Colors.blue.withOpacity(.1),
                              shape: BoxShape.circle,
                            ),

                            child: Center(
                              child: Text(
                                "${percent.toStringAsFixed(0)}%",
                                style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ),

                          const SizedBox(width: 15),

                          /// Text Info
                          Expanded(
                            child: Column(
                              crossAxisAlignment:
                                  CrossAxisAlignment.start,
                              children: [

                                Text(
                                  "Score: ${item["score"]}/${item["total"]}",
                                  style: const TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),

                                const SizedBox(height: 5),

                                Text(
                                  item["date"],
                                  style: const TextStyle(
                                    color: Colors.grey,
                                  ),
                                )

                              ],
                            ),
                          ),

                          const Icon(
                            Icons.arrow_forward_ios,
                            size: 16,
                            color: Colors.grey,
                          )

                        ],
                      ),
                    ),
                  ),
                );
              },
            ),
    );
  }
}