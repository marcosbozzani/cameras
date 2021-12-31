using System.Threading.Tasks;

namespace Duck.Cameras.Windows.Model
{
    public static class AsyncExtensions
    {
        public static void FireAndForget(this Task task)
        {
            task.ConfigureAwait(false);
        }
    }
}
