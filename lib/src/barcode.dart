/// Represents a scanned barcode.
///
/// - [data]: the decoded content of the barcode
/// - [type]: the barcode symbology as a human-readable string
class Barcode {
  /// Decoded barcode content
  String data;

  /// Symbology type name (e.g., CODE128, EAN13)
  String type;

  Barcode({required this.data, required this.type});
}
