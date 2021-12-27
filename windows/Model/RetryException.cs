using System;
using System.Collections.Generic;

namespace Duck.Cameras.Windows.Model
{
    public class RetryException : Exception
    {
        private readonly List<Exception> originalExceptions;

        public RetryException(List<Exception> originalExceptions)
        {
            this.originalExceptions = originalExceptions;
        }

        public List<Exception> getOriginalExceptions()
        {
            return originalExceptions;
        }
    }
}
