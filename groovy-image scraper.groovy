@Grab('org.jsoup:jsoup:1.14.3')

import org.jsoup.Jsoup
import java.nio.file.Files
import java.nio.file.Paths
import java.net.URI

def destinationDirectory = System.getProperty('user.home') + '/Downloads' // Destination directory for saving the image

// Create the destination directory if it doesn't exist
Files.createDirectories(Paths.get(destinationDirectory))

// Prompt the user to enter the URL
def url = System.console().readLine('Enter the URL of the website to scrape: ')

try {
    // Connect to the webpage using Jsoup
    def doc = Jsoup.connect(url).get()

    // Find the image element and extract its source URL
    def imageElement = doc.select('img').first()
    def imageUrl = imageElement.attr('src')

    // Convert relative URL to absolute URL
    def baseUri = doc.baseUri()
    def absoluteImageUrl = URI.create(baseUri).resolve(imageUrl).toString()

    // Download the image
    def imageData = Jsoup.connect(absoluteImageUrl).ignoreContentType(true).execute().bodyAsBytes()

    // Extract the filename from the image URL
    def filename = absoluteImageUrl.substring(absoluteImageUrl.lastIndexOf('/') + 1)
    filename = filename.replaceAll("[\\\\/:*?\"<>|]", "") // Remove illegal characters from the filename

    // Save the image to the destination directory
    def destinationPath = Paths.get(destinationDirectory, filename)
    Files.write(destinationPath, imageData)

    println "Image downloaded successfully. Saved as: $destinationPath"
} catch (Exception e) {
    println "Error occurred: ${e.message}"
}
