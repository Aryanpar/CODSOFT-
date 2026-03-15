import 'package:flutter/material.dart';

import '../models/question_model.dart';
import '../services/api_service.dart';
import 'result_screen.dart';

class QuizScreen extends StatefulWidget {
  const QuizScreen({super.key});

  @override
  State<QuizScreen> createState() => _QuizScreenState();
}

class _QuizScreenState extends State<QuizScreen> {

  List<Question> questions = [];
  bool isLoading = true;

  int currentQuestion = 0;
  int score = 0;

  int? selectedIndex;

  @override
  void initState() {
    super.initState();
    loadQuestions();
  }

  void loadQuestions() async {
    questions = await ApiService.fetchQuestions();

    setState(() {
      isLoading = false;
    });
  }

  void answerQuestion(int index) {

    setState(() {
      selectedIndex = index;
    });

    Future.delayed(const Duration(milliseconds: 300), () {

      if (index == questions[currentQuestion].correctIndex) {
        score++;
      }

      if (currentQuestion < questions.length - 1) {

        setState(() {
          currentQuestion++;
          selectedIndex = null;
        });

      } else {

        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => ResultScreen(
              score: score,
              total: questions.length,
            ),
          ),
        );

      }

    });
  }

  @override
  Widget build(BuildContext context) {

    if (isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    Question question = questions[currentQuestion];

    return Scaffold(
      appBar: AppBar(
        title: const Text("Quiz"),
        centerTitle: true,
      ),

      body: Padding(
        padding: const EdgeInsets.all(24),

        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [

            /// Progress
            LinearProgressIndicator(
              value: (currentQuestion + 1) / questions.length,
              minHeight: 8,
              borderRadius: BorderRadius.circular(8),
            ),

            const SizedBox(height: 20),

            Text(
              "Question ${currentQuestion + 1} of ${questions.length}",
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w500,
              ),
            ),

            const SizedBox(height: 30),

            /// Question Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(22),

              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(18),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(.08),
                    blurRadius: 10,
                    offset: const Offset(0,4),
                  )
                ],
              ),

              child: Text(
                question.question,
                style: const TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),

            const SizedBox(height: 30),

            /// Options
            Expanded(
              child: ListView(
                children: question.options
                    .asMap()
                    .entries
                    .map((option) {

                  bool isSelected = selectedIndex == option.key;

                  return OptionButton(
                    text: option.value,
                    isSelected: isSelected,
                    onTap: () {
                      answerQuestion(option.key);
                    },
                  );

                }).toList(),
              ),
            )

          ],
        ),
      ),
    );
  }
}

class OptionButton extends StatelessWidget {

  final String text;
  final bool isSelected;
  final VoidCallback onTap;

  const OptionButton({
    super.key,
    required this.text,
    required this.onTap,
    required this.isSelected,
  });

  @override
  Widget build(BuildContext context) {

    return GestureDetector(
      onTap: onTap,

      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        margin: const EdgeInsets.only(bottom: 16),
        padding: const EdgeInsets.symmetric(
          horizontal: 18,
          vertical: 18,
        ),

        decoration: BoxDecoration(
          color: isSelected ? Colors.blue.withOpacity(.15) : Colors.white,
          borderRadius: BorderRadius.circular(14),
          border: Border.all(
            color: isSelected ? Colors.blue : Colors.grey.shade300,
            width: 1.5,
          ),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(.05),
              blurRadius: 6,
              offset: const Offset(0,3),
            )
          ],
        ),

        child: Text(
          text,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    );
  }
}