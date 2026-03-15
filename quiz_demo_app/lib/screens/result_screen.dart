import 'package:flutter/material.dart';

import '../data/history_data.dart';
import 'history_screen.dart';

class ResultScreen extends StatefulWidget {

  final int score;
  final int total;

  const ResultScreen({
    super.key,
    required this.score,
    required this.total,
  });

  @override
  State<ResultScreen> createState() => _ResultScreenState();
}

class _ResultScreenState extends State<ResultScreen>
    with SingleTickerProviderStateMixin {

  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();

    /// Save history
    quizHistory.add({
      "score": widget.score,
      "total": widget.total,
      "date": DateTime.now().toString(),
    });

    double percent = widget.score / widget.total;

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );

    _animation = Tween<double>(
      begin: 0,
      end: percent,
    ).animate(
      CurvedAnimation(
        parent: _controller,
        curve: Curves.easeOut,
      ),
    );

    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      backgroundColor: const Color(0xffF5F7FA),

      appBar: AppBar(
        title: const Text("Quiz Result"),
        centerTitle: true,
      ),

      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),

          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [

              const Icon(
                Icons.emoji_events,
                size: 90,
                color: Colors.amber,
              ),

              const SizedBox(height: 20),

              const Text(
                "Quiz Completed!",
                style: TextStyle(
                  fontSize: 26,
                  fontWeight: FontWeight.bold,
                ),
              ),

              const SizedBox(height: 40),

              /// Animated Score Circle
              AnimatedBuilder(
                animation: _animation,
                builder: (context, child) {

                  return SizedBox(
                    height: 160,
                    width: 160,

                    child: Stack(
                      alignment: Alignment.center,
                      children: [

                        SizedBox(
                          height: 160,
                          width: 160,

                          child: CircularProgressIndicator(
                            value: _animation.value,
                            strokeWidth: 12,
                            backgroundColor: Colors.grey.shade200,
                          ),
                        ),

                        Center(
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [

                              Text(
                                "${(_animation.value * 100).toStringAsFixed(0)}%",
                                style: const TextStyle(
                                  fontSize: 30,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),

                              const SizedBox(height: 4),

                              Text(
                                "${widget.score}/${widget.total}",
                                style: const TextStyle(
                                  fontSize: 16,
                                  color: Colors.grey,
                                ),
                              )

                            ],
                          ),
                        )

                      ],
                    ),
                  );
                },
              ),

              const SizedBox(height: 50),

              /// Back Home Button
              SizedBox(
                width: double.infinity,

                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.all(16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),

                  onPressed: () {
                    Navigator.pop(context);
                  },

                  child: const Text(
                    "Back to Home",
                    style: TextStyle(fontSize: 18),
                  ),
                ),
              ),

              const SizedBox(height: 14),

              /// View History Button
              SizedBox(
                width: double.infinity,

                child: OutlinedButton(
                  style: OutlinedButton.styleFrom(
                    padding: const EdgeInsets.all(16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),

                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const HistoryScreen(),
                      ),
                    );
                  },

                  child: const Text(
                    "View History",
                    style: TextStyle(fontSize: 18),
                  ),
                ),
              ),

            ],
          ),
        ),
      ),
    );
  }
}