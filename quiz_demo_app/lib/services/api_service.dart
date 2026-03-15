import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/question_model.dart';

class ApiService {

  static const String apiUrl =
      "https://opentdb.com/api.php?amount=10&type=multiple";

  static Future<List<Question>> fetchQuestions() async {

    try {

      final response = await http.get(Uri.parse(apiUrl));

      if (response.statusCode != 200) {
        throw Exception("Failed to load questions");
      }

      final data = jsonDecode(response.body);

      List results = data["results"];

      return results.map<Question>((q) {

        // Combine options
        List<String> options = List<String>.from(q["incorrect_answers"]);
        options.add(q["correct_answer"]);
        options.shuffle();

        return Question(
          question: _decodeHtml(q["question"]),
          options: options.map((o) => _decodeHtml(o)).toList(),
          correctIndex: options.indexOf(q["correct_answer"]),
        );

      }).toList();

    } catch (e) {
      throw Exception("Error fetching quiz questions: $e");
    }
  }

  // Fix HTML entities from OpenTDB
  static String _decodeHtml(String text) {
    return text
        .replaceAll("&quot;", '"')
        .replaceAll("&#039;", "'")
        .replaceAll("&amp;", "&")
        .replaceAll("&lt;", "<")
        .replaceAll("&gt;", ">");
  }
}