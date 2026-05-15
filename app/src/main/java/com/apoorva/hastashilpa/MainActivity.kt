package com.apoorva.hastashilpa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.net.URLDecoder
import java.net.URLEncoder

// ─────────────────────────────────────────────
//  THEME COLORS
// ─────────────────────────────────────────────

val OrangePrimary   = Color(0xFFE65100)
val OrangeLight     = Color(0xFFFF6F00)
val OrangeAccent    = Color(0xFFFFB300)
val BgWarm          = Color(0xFFFFF8F0)
val BgCard          = Color(0xFFFFF3E0)
val TextDark        = Color(0xFF3E2723)
val TextMuted       = Color(0xFF795548)
val GreenSuccess    = Color(0xFF2E7D32)

// ─────────────────────────────────────────────
//  ACTIVITY
// ─────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HastaShilpaApp() }
    }
}

// ─────────────────────────────────────────────
//  DATA MODEL
// ─────────────────────────────────────────────

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val priceValue: Int,          // numeric price for cart math
    val rating: String,
    val description: String,
    val category: String,
    val image: Int,
    val artisan: String = "Local Artisan"
)

// Shared app-level state (simple singleton-style; use ViewModel in production)
object AppState {
    val favorites  = mutableStateListOf<Int>()   // product IDs
    val cartItems  = mutableStateListOf<Int>()   // product IDs
}

// ─────────────────────────────────────────────
//  PRODUCT DATA
// ─────────────────────────────────────────────

val sampleProducts = listOf(
    Product(1,  "Handmade Basket",  "₹499",  499,  "4.8", "Eco-friendly bamboo basket woven by skilled artisans.",       "Home Decor",  R.drawable.basket,    "Meera Devi"),
    Product(2,  "Wooden Lamp",      "₹899",  899,  "4.7", "Traditional wooden lamp with intricate carving.",             "Lighting",    R.drawable.lamp,      "Rajan Kumar"),
    Product(3,  "Clay Pot",         "₹299",  299,  "4.5", "Handcrafted clay pot, perfect for plants or kitchen.",        "Kitchen",     R.drawable.claypot,   "Sunita Bai"),
    Product(4,  "Decor Item",       "₹699",  699,  "4.6", "Beautiful home decor piece with tribal motifs.",              "Home Decor",  R.drawable.decor,     "Anita Sharma"),
    Product(5,  "Pen Holder",       "₹199",  199,  "4.4", "Wooden handmade pen holder with ethnic engravings.",          "Stationery",  R.drawable.penholder, "Ramesh Ji"),
    Product(6,  "Flower Vase",      "₹599",  599,  "4.5", "Decorative handmade vase with colorful enamel finish.",       "Home Decor",  R.drawable.vase,      "Priya Arts"),
    Product(7,  "Serving Tray",     "₹799",  799,  "4.6", "Wooden serving tray with beautiful inlay work.",              "Kitchen",     R.drawable.tray,      "Craft Circle"),
    Product(8,  "Wooden Chair",     "₹2499", 2499, "4.9", "Premium wooden chair with hand-painted folk art designs.",    "Furniture",   R.drawable.chair,     "Master Craftsmen"),
    Product(9,  "Handbag",          "₹1299", 1299, "4.7", "Handcrafted traditional handbag with ethnic embroidery.",     "Fashion",     R.drawable.handbag,   "Craft Women"),
    Product(10, "Stand",            "₹999",  999,  "4.6", "Decorative wooden stand with intricate carvings.",            "Home Decor",  R.drawable.stand,     "Wood Masters")
    )

val categories = listOf("All", "Home Decor", "Kitchen", "Lighting", "Stationery", "Furniture", "Fashion")

// ─────────────────────────────────────────────
//  NAV ROUTES
// ─────────────────────────────────────────────

object Routes {
    const val SPLASH      = "splash"
    const val HOME        = "home"
    const val FAVORITES   = "favorites"
    const val EXPLORE     = "explore"
    const val PROFILE     = "profile"
    const val CALCULATOR  = "calculator"
    const val CART        = "cart"
    const val DETAIL      = "detail/{productId}"
    const val RESULT      = "result/{finalPrice}"

    fun detail(id: Int)         = "detail/$id"
    fun result(price: Int)      = "result/$price"
}

// ─────────────────────────────────────────────
//  MAIN APP / NAV HOST
// ─────────────────────────────────────────────

@Composable
fun HastaShilpaApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(navController)
        }

        composable(Routes.EXPLORE) {
            ExploreScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }

        composable(Routes.CALCULATOR) {
            CalculatorScreen(navController)
        }

        composable(Routes.CART) {
            CartScreen(navController)
        }

        composable(Routes.DETAIL) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            val product   = sampleProducts.find { it.id == productId }
            if (product != null) {
                DetailScreen(navController = navController, product = product)
            } else {
                NotFoundScreen(navController)
            }
        }

        composable(Routes.RESULT) { backStackEntry ->
            val finalPrice = backStackEntry.arguments?.getString("finalPrice") ?: "0"
            ResultScreen(navController = navController, finalPrice = finalPrice)
        }
    }
}

// ─────────────────────────────────────────────
//  BOTTOM NAV BAR  (reusable)
// ─────────────────────────────────────────────

data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        NavItem("Home",      Icons.Default.Home,         Routes.HOME),
        NavItem("Explore",   Icons.Default.Search,       Routes.EXPLORE),
        NavItem("Favorites", Icons.Default.Favorite,     Routes.FAVORITES),
        NavItem("Cart",      Icons.Default.ShoppingCart, Routes.CART),
        NavItem("Profile",   Icons.Default.Person,       Routes.PROFILE)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    // Show badge on Cart if items present
                    if (item.route == Routes.CART && AppState.cartItems.isNotEmpty()) {
                        BadgedBox(badge = {
                            Badge { Text(AppState.cartItems.size.toString()) }
                        }) {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label    = { Text(item.label, fontSize = 11.sp) },
                selected = currentRoute == item.route,
                onClick  = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = OrangePrimary,
                    selectedTextColor   = OrangePrimary,
                    indicatorColor      = Color(0xFFFFE0B2),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

// ─────────────────────────────────────────────
//  SPLASH SCREEN
// ─────────────────────────────────────────────

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8F0), Color(0xFFFFE0B2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Logo placeholder circle
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("🏺", fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text       = "Hasta-Shilpa",
                fontSize   = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = OrangePrimary
            )

            Text(
                text      = "Handcrafted with Love",
                fontSize  = 16.sp,
                color     = TextMuted,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { navController.navigate(Routes.HOME) },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp),
                shape  = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Start Exploring", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  HOME SCREEN
// ─────────────────────────────────────────────

@Composable
fun HomeScreen(navController: NavHostController) {
    var searchText      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredProducts = sampleProducts.filter { product ->
        val matchesSearch   = product.name.contains(searchText, ignoreCase = true) ||
                product.description.contains(searchText, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = BgWarm
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Header row
            Row(
                modifier       = Modifier.fillMaxWidth(),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hasta-Shilpa", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
                    Text("Discover handcrafted treasures", fontSize = 13.sp, color = TextMuted)
                }
                // Cart icon shortcut
                IconButton(onClick = { navController.navigate(Routes.CART) }) {
                    BadgedBox(
                        badge = {
                            if (AppState.cartItems.isNotEmpty())
                                Badge { Text(AppState.cartItems.size.toString()) }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = OrangePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search bar
            OutlinedTextField(
                value          = searchText,
                onValueChange  = { searchText = it },
                modifier       = Modifier.fillMaxWidth(),
                placeholder    = { Text("Search handcrafted products…") },
                leadingIcon    = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon   = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape          = RoundedCornerShape(20.dp),
                singleLine     = true,
                colors         = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = OrangePrimary,
                    unfocusedBorderColor = Color(0xFFBCAAA4)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val selected = cat == selectedCategory
                    FilterChip(
                        selected = selected,
                        onClick  = { selectedCategory = cat },
                        label    = { Text(cat) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor    = OrangePrimary,
                            selectedLabelColor        = Color.White,
                            containerColor            = Color(0xFFFFE0B2),
                            labelColor                = TextDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Result count
            Text(
                "${filteredProducts.size} product${if (filteredProducts.size != 1) "s" else ""} found",
                fontSize = 13.sp, color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredProducts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😔", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No products found", fontSize = 18.sp, color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding      = PaddingValues(bottom = 8.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(product = product, navController = navController)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  PRODUCT CARD
// ─────────────────────────────────────────────

@Composable
fun ProductCard(product: Product, navController: NavHostController) {
    val isFav    = AppState.favorites.contains(product.id)
    val inCart   = AppState.cartItems.contains(product.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Routes.detail(product.id)) },
        colors    = CardDefaults.cardColors(containerColor = BgCard),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Product image
            Box {
                Image(
                    painter           = painterResource(id = product.image),
                    contentDescription = product.name,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )
                // Category badge
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                        .background(OrangePrimary.copy(alpha = 0.88f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(product.category, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                // Favorite button
                IconButton(
                    onClick  = {
                        if (isFav) AppState.favorites.remove(product.id)
                        else       AppState.favorites.add(product.id)
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector        = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint               = if (isFav) Color.Red else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(product.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("by ${product.artisan}", fontSize = 12.sp, color = TextMuted, fontStyle = FontStyle.Italic)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(product.price, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
                    Text("⭐ ${product.rating}", fontSize = 14.sp, color = TextDark)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    product.description,
                    fontSize  = 13.sp,
                    color     = TextMuted,
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(10.dp))
                // Add to cart button
                Button(
                    onClick  = {
                        if (inCart) AppState.cartItems.remove(product.id)
                        else        AppState.cartItems.add(product.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = if (inCart) GreenSuccess else OrangePrimary
                    )
                ) {
                    Icon(
                        if (inCart) Icons.Default.Check else Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (inCart) "In Cart" else "Add to Cart")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  DETAIL SCREEN
// ─────────────────────────────────────────────

@Composable
fun DetailScreen(navController: NavHostController, product: Product) {
    val isFav  = AppState.favorites.contains(product.id)
    val inCart = AppState.cartItems.contains(product.id)

    Scaffold(
        topBar = {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .background(BgWarm)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
                }
                Text(
                    "Product Details",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextDark,
                    modifier   = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (isFav) AppState.favorites.remove(product.id)
                    else       AppState.favorites.add(product.id)
                }) {
                    Icon(
                        if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint               = if (isFav) Color.Red else TextMuted
                    )
                }
            }
        },
        containerColor = BgWarm
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // Product image
                Image(
                    painter            = painterResource(id = product.image),
                    contentDescription = product.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {

                    // Category chip
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(product.category, fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(product.name, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                    Text("by ${product.artisan}", fontSize = 14.sp, color = TextMuted, fontStyle = FontStyle.Italic)

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        Text(product.price, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
                        RatingBadge(product.rating)
                    }

                    Spacer(Modifier.height(16.dp))

                    Divider(color = Color(0xFFFFCCBC))

                    Spacer(Modifier.height(16.dp))

                    Text("About this product", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(6.dp))
                    Text(product.description, fontSize = 15.sp, color = TextMuted, lineHeight = 22.sp)

                    Spacer(Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Add to Cart
                        Button(
                            onClick  = {
                                if (inCart) AppState.cartItems.remove(product.id)
                                else        AppState.cartItems.add(product.id)
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = if (inCart) GreenSuccess else OrangePrimary
                            )
                        ) {
                            Icon(
                                if (inCart) Icons.Default.Check else Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier           = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (inCart) "In Cart" else "Add to Cart", fontWeight = FontWeight.Bold)
                        }

                        // Estimate price
                        OutlinedButton(
                            onClick  = { navController.navigate(Routes.CALCULATOR) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Price Calc", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBadge(rating: String) {
    Row(
        modifier          = Modifier
            .background(Color(0xFFFFF9C4), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⭐", fontSize = 16.sp)
        Spacer(Modifier.width(4.dp))
        Text(rating, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
    }
}

// ─────────────────────────────────────────────
//  FAVORITES SCREEN
// ─────────────────────────────────────────────

@Composable
fun FavoritesScreen(navController: NavHostController) {
    val favoriteProducts = sampleProducts.filter { AppState.favorites.contains(it.id) }

    Scaffold(
        bottomBar      = { BottomNavBar(navController) },
        containerColor = BgWarm
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("❤️ Favorites", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
            Spacer(Modifier.height(14.dp))

            if (favoriteProducts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🤍", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No favorites yet!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(Modifier.height(6.dp))
                        Text("Tap the ♡ on any product to save it here.", fontSize = 14.sp, color = TextMuted, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }},
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) { Text("Browse Products") }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(favoriteProducts, key = { it.id }) { product ->
                        ProductCard(product = product, navController = navController)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  EXPLORE SCREEN
// ─────────────────────────────────────────────

@Composable
fun ExploreScreen(navController: NavHostController) {
    Scaffold(
        bottomBar      = { BottomNavBar(navController) },
        containerColor = BgWarm
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("🔍 Explore by Category", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
            Spacer(Modifier.height(6.dp))
            Text("Discover handcrafted goods by type", fontSize = 14.sp, color = TextMuted)
            Spacer(Modifier.height(20.dp))

            // Category grid (2 columns)
            val cats = categories.filter { it != "All" }
            val catColors = listOf(
                Color(0xFFFFE0B2), Color(0xFFF8BBD0), Color(0xFFB2DFDB),
                Color(0xFFE1BEE7), Color(0xFFB3E5FC)
            )
            val catEmojis = mapOf(
                "Home Decor" to "🏠", "Kitchen" to "🍳", "Fashion" to "👜",
                "Lighting"   to "💡", "Stationery" to "✏️", "Furniture" to "🪑"
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cats.chunked(2)) { rowCats ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCats.forEachIndexed { idx, cat ->
                            val count = sampleProducts.count { it.category == cat }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp)
                                    .clickable {
                                        navController.navigate(Routes.HOME)
                                    },
                                colors    = CardDefaults.cardColors(containerColor = catColors[cats.indexOf(cat) % catColors.size]),
                                shape     = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(catEmojis[cat] ?: "🎨", fontSize = 34.sp)
                                        Spacer(Modifier.height(6.dp))
                                        Text(cat, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text("$count items", fontSize = 12.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                        // If odd number, add a spacer
                        if (rowCats.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  PROFILE SCREEN
// ─────────────────────────────────────────────

@Composable
fun ProfileScreen(navController: NavHostController) {
    Scaffold(
        bottomBar      = { BottomNavBar(navController) },
        containerColor = BgWarm
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(listOf(OrangeLight, OrangePrimary))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 46.sp)
            }

            Spacer(Modifier.height(14.dp))
            Text("Artisan Member", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text("Supporting Indian handicrafts since 2024", fontSize = 13.sp, color = TextMuted)

            Spacer(Modifier.height(24.dp))

            // Stats row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat("${AppState.favorites.size}", "Favorites")
                ProfileStat("${AppState.cartItems.size}", "In Cart")
                ProfileStat("${sampleProducts.size}", "Products")
            }

            Spacer(Modifier.height(28.dp))

            // Menu items
            val menuItems = listOf(
                Triple(Icons.Default.ShoppingCart,  "My Cart",           Routes.CART),
                Triple(Icons.Default.Favorite,       "My Favorites",      Routes.FAVORITES),
                Triple(Icons.Default.Calculate,      "Price Calculator",  Routes.CALCULATOR),
                Triple(Icons.Default.Explore,        "Explore Products",  Routes.EXPLORE)
            )

            menuItems.forEach { (icon, title, route) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { navController.navigate(route) },
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    shape     = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = title, tint = OrangePrimary)
                        Spacer(Modifier.width(14.dp))
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextDark, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
        Text(label, fontSize = 13.sp, color = TextMuted)
    }
}

// ─────────────────────────────────────────────
//  CART SCREEN
// ─────────────────────────────────────────────

@Composable
fun CartScreen(navController: NavHostController) {
    val cartProducts = sampleProducts.filter { AppState.cartItems.contains(it.id) }
    val total        = cartProducts.sumOf { it.priceValue }

    Scaffold(
        bottomBar      = { BottomNavBar(navController) },
        containerColor = BgWarm
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("🛒 My Cart", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
            Spacer(Modifier.height(14.dp))

            if (cartProducts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛒", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Your cart is empty", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(Modifier.height(6.dp))
                        Text("Add products from the home screen.", fontSize = 14.sp, color = TextMuted)
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }},
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) { Text("Shop Now") }
                    }
                }
            } else {
                LazyColumn(
                    modifier            = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding      = PaddingValues(bottom = 8.dp)
                ) {
                    items(cartProducts, key = { it.id }) { product ->
                        CartItemRow(product = product)
                    }
                }

                // Order summary card
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    shape     = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Items (${cartProducts.size})", color = TextMuted)
                            Text("₹$total", fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery", color = TextMuted)
                            Text("FREE", color = GreenSuccess, fontWeight = FontWeight.Bold)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text("₹$total", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = OrangePrimary)
                        }
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick  = {
                                // Simple checkout: navigate to result with total
                                navController.navigate(Routes.result(total))
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Proceed to Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CartItemRow(product: Product) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = BgCard),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter            = painterResource(id = product.image),
                contentDescription = product.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(product.artisan, fontSize = 12.sp, color = TextMuted, fontStyle = FontStyle.Italic)
                Spacer(Modifier.height(4.dp))
                Text(product.price, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
            }
            IconButton(onClick = { AppState.cartItems.remove(product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  CALCULATOR SCREEN
// ─────────────────────────────────────────────

@Composable
fun CalculatorScreen(navController: NavHostController) {
    var materialCost  by remember { mutableStateOf("") }
    var laborCost     by remember { mutableStateOf("") }
    var overheadCost  by remember { mutableStateOf("") }
    var profitPercent by remember { mutableStateOf("20") }
    var errorMessage  by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .background(BgWarm)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
                }
                Text("Price Calculator", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        },
        containerColor = BgWarm
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Calculate the fair selling price for your handcrafted product.",
                    fontSize  = 14.sp,
                    color     = TextMuted,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(20.dp))

                CalcField(label = "Material Cost (₹)", value = materialCost, onValueChange = { materialCost = it })
                Spacer(Modifier.height(14.dp))
                CalcField(label = "Labor / Time Cost (₹)", value = laborCost, onValueChange = { laborCost = it })
                Spacer(Modifier.height(14.dp))
                CalcField(label = "Overhead / Misc Cost (₹) — optional", value = overheadCost, onValueChange = { overheadCost = it })
                Spacer(Modifier.height(14.dp))
                CalcField(label = "Profit Margin (%)", value = profitPercent, onValueChange = { profitPercent = it })

                Spacer(Modifier.height(10.dp))

                AnimatedVisibility(visible = errorMessage.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                            Spacer(Modifier.width(8.dp))
                            Text(errorMessage, color = Color.Red, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Validation
                        if (materialCost.isEmpty() || laborCost.isEmpty() || profitPercent.isEmpty()) {
                            errorMessage = "Please fill Material Cost, Labor Cost, and Profit %"
                            return@Button
                        }
                        val material  = materialCost.toDoubleOrNull()
                        val labor     = laborCost.toDoubleOrNull()
                        val overhead  = if (overheadCost.isEmpty()) 0.0 else overheadCost.toDoubleOrNull()
                        val profit    = profitPercent.toDoubleOrNull()

                        when {
                            material == null || labor == null || overhead == null || profit == null ->
                                errorMessage = "Invalid number — please enter digits only"
                            material < 0 || labor < 0 || overhead < 0 ->
                                errorMessage = "Costs cannot be negative"
                            profit < 0 || profit > 500 ->
                                errorMessage = "Profit margin must be between 0% and 500%"
                            else -> {
                                errorMessage = ""
                                val total      = material + labor + overhead
                                val profitAmt  = total * (profit / 100.0)
                                val finalPrice = total + profitAmt
                                navController.navigate(Routes.result(finalPrice.toInt()))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Calculate Final Price", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CalcField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label) },
        modifier       = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape          = RoundedCornerShape(14.dp),
        singleLine     = true,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = OrangePrimary,
            unfocusedBorderColor = Color(0xFFBCAAA4),
            focusedLabelColor    = OrangePrimary
        )
    )
}

// ─────────────────────────────────────────────
//  RESULT SCREEN
// ─────────────────────────────────────────────

@Composable
fun ResultScreen(navController: NavHostController, finalPrice: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8F0), Color(0xFFFFE0B2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth(0.88f)
                .padding(horizontal = 8.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            shape     = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier            = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉", fontSize = 56.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Suggested Selling Price",
                    fontSize  = 18.sp,
                    color     = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "₹ $finalPrice",
                    fontSize   = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = OrangePrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "This includes your costs + profit margin",
                    fontSize  = 13.sp,
                    color     = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))

                Button(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Back to Calculator", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick  = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Go to Home", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  404 / NOT FOUND SCREEN
// ─────────────────────────────────────────────

@Composable
fun NotFoundScreen(navController: NavHostController) {
    Box(
        modifier           = Modifier.fillMaxSize().background(BgWarm),
        contentAlignment   = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("😕", fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text("Product not found", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) { Text("Go Back") }
        }
    }
}