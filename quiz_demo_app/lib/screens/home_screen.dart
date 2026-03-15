import 'package:flutter/material.dart';

import 'history_screen.dart';
import 'quiz_screen.dart';


class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  bool isDarkMode = false;

  @override
  Widget build(BuildContext context) {

    Color bgColor = isDarkMode ? const Color(0xff121212) : const Color(0xffF5F7FA);
    Color cardColor = isDarkMode ? const Color(0xff1E1E1E) : Colors.white;
    Color textColor = isDarkMode ? Colors.white : Colors.black87;

    return Scaffold(
      backgroundColor: bgColor,

      appBar: AppBar(
        elevation: 0,
        backgroundColor: bgColor,
        centerTitle: true,

        title: Text(
          "Quiz Master",
          style: TextStyle(
            color: textColor,
            fontWeight: FontWeight.bold,
          ),
        ),

        actions: [

          Icon(
            isDarkMode ? Icons.dark_mode : Icons.light_mode,
            color: textColor,
          ),

          Switch(
            value: isDarkMode,
            onChanged: (value) {
              setState(() {
                isDarkMode = value;
              });
            },
          ),

          const SizedBox(width: 10)

        ],
      ),

      body: Padding(
        padding: const EdgeInsets.all(24),

        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [

            /// Logo
            Icon(
              Icons.quiz_outlined,
              size: 90,
              color: textColor,
            ),

            const SizedBox(height: 20),

            Text(
              "Challenge Your Mind",
              style: TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: textColor,
              ),
            ),

            const SizedBox(height: 10),

            Text(
              "Play quizzes and track your progress",
              style: TextStyle(
                fontSize: 16,
                color: textColor.withOpacity(.6),
              ),
            ),

            const SizedBox(height: 40),

            /// Start Quiz
            _menuCard(
              icon: Icons.play_arrow_rounded,
              title: "Start Quiz",
              color: cardColor,
              textColor: textColor,
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const QuizScreen(),
                  ),
                );
              },
            ),

            const SizedBox(height: 16),

            /// History
            _menuCard(
              icon: Icons.history,
              title: "View History",
              color: cardColor,
              textColor: textColor,
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const HistoryScreen(),
                  ),
                );
              },
            ),

          ],
        ),
      ),
    );
  }

  Widget _menuCard({
    required IconData icon,
    required String title,
    required VoidCallback onTap,
    required Color color,
    required Color textColor,
  }) {

    return InkWell(
      borderRadius: BorderRadius.circular(16),
      onTap: onTap,

      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(
          horizontal: 20,
          vertical: 18,
        ),

        decoration: BoxDecoration(
          color: color,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(.05),
              blurRadius: 8,
              offset: const Offset(0,3),
            )
          ],
        ),

        child: Row(
          children: [

            Icon(icon, size: 28, color: textColor),

            const SizedBox(width: 14),

            Text(
              title,
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: textColor,
              ),
            ),

            const Spacer(),

            Icon(
              Icons.arrow_forward_ios,
              size: 16,
              color: textColor.withOpacity(.5),
            )

          ],
        ),
      ),
    );
  }
}