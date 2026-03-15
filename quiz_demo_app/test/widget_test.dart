import 'package:flutter_test/flutter_test.dart';
import 'package:quiz_demo_app/main.dart';

void main() {
  testWidgets('Quiz app loads correctly', (WidgetTester tester) async {

    // Build the Quiz App
    await tester.pumpWidget(const QuizApp());

    // Check if Start Quiz button exists
    expect(find.text('Start Quiz'), findsOneWidget);

  });
}